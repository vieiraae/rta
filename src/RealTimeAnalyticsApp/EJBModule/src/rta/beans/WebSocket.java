/*
MIT License

Copyright (c) 2016 Alexandre Vieira https://github.com/vieiraae/rta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package rta.beans;

import rta.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.Session;

@Startup
@Singleton
public class WebSocket {
    public WebSocket() {
        super();
    }
    
    private static boolean USE_SCHEDULER = false;
    private static int SCHEDULER_INITIAL_DELAY = 5000;
    private static int SCHEDULER_PERIOD = 500;
    
    private static final Map<Session, String> map = Collections.synchronizedMap(new HashMap<Session, String>());
    
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    

    @PostConstruct
    private void startScheduler() {
      if (USE_SCHEDULER) {
          final Runnable runnable = new Runnable() {
            public void run() {            
                try {
                    
                    for (Map.Entry<Session, String> entry : map.entrySet()) {
                        if (!entry.getKey().isOpen())
                            map.remove(entry.getKey());
                        else if (entry.getValue() != null && entry.getValue().length() > 0) {
                            entry.getKey().getBasicRemote().sendText(entry.getValue());
                            entry.setValue("");
                        }
                    }
                } catch (Exception e) {
                        Logger.logException("Exception caught on scheduler runnable", e);
                }
            }
          };
          final ScheduledFuture<?> schedulerHandle = scheduler.scheduleAtFixedRate(runnable, SCHEDULER_INITIAL_DELAY, SCHEDULER_PERIOD, TimeUnit.MILLISECONDS);
          Logger.notice("Javascript invocation Scheduler started");
      }
    }    
    
    @PreDestroy
    private void stopScheduler() {
        if (USE_SCHEDULER) {
            scheduler.shutdownNow();
            Logger.notice("Javascript invocation Scheduler destroyed");
        }
    }
    
    public static void invokeJS(Session session, String statement) throws Exception {
        session.getBasicRemote().sendText(statement);
    }    
    
    public static void invokeJS(Session session, String function, String jsonData) throws Exception {
        invokeJS(session, "window.parent." + function + "(" + jsonData + ");\n");
    }    

    @Lock(LockType.WRITE)
    public static void scheduleInvokeJS(Set<Session> sessions, String statement) throws Exception {
        for  (Session sess : sessions){
            if (USE_SCHEDULER) {
                if (sess.isOpen()) {
                    if (map.get(sess) == null)
                        map.put(sess, statement);
                    else
                        map.put(sess, map.get(sess) + statement);
                }
            } else {
                invokeJS(sess, statement);
            }
            
        }
    }    

    @Lock(LockType.WRITE)
    public static void scheduleInvokeJS(Set<Session> sessions, String function, String jsonData) throws Exception {
        scheduleInvokeJS(sessions, "window.parent." + function + "(" + jsonData + ");\n");
    }    
    
    @Lock(LockType.WRITE)
    public static void scheduleInvokeJS(Set<Session> sessions, String function, String id, String jsonData) throws Exception {
        scheduleInvokeJS(sessions, "window.parent." + function + "('" + id + "'," + jsonData + ");\n");
    }    

    @Lock(LockType.WRITE)
    public static void scheduleInvokeJS(Set<Session> sessions, String function, String type, String name, String jsonData) throws Exception {
        scheduleInvokeJS(sessions, "window.parent." + function + "('" + type + "','" + name + "'," + jsonData + ");\n");
    }    

    public static String getUser(Session session) {
        return session.getUserPrincipal().getName();
    }    
}
