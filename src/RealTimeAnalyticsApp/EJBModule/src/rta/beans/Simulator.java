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

import com.tangosol.util.Filter;
import com.tangosol.util.filter.EqualsFilter;

import rta.Logger;

import rta.datatypes.EventKey;
import rta.datatypes.Notification;
import rta.datatypes.Aggregation;

import java.io.StringReader;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.websocket.Session;

import rta.datatypes.Aggregation;
import rta.datatypes.KPI;
import rta.datatypes.Scalar;


@Startup
@Singleton
public class Simulator {
     
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> schedulerHandle = null;
    private static Session schedulerSession = null;
    private static int schedulerExecCount = 0;
    private static String activeDashboard = null;
    private static final int SCHEDULER_INITIAL_DELAY = 1000;
    

    final Runnable runnable = new Runnable() {
      public void run() {            
          try {
              if (schedulerSession != null && schedulerHandle != null) {
                  if (schedulerSession.isOpen()) {
                        runSimulator();
                        schedulerExecCount++;
                        WebSocket.invokeJS(schedulerSession, "simulator.updateStatus", "'" + activeDashboard + " # exec(s): " + schedulerExecCount + "'");
                  } else {
                      terminate();
                  }
              }              
          } catch (Exception e) {
                  Logger.logException("Exception caught on scheduler runnable", e);
          }
      }
    };
    
    public void runSimulator() throws Exception {
        if (activeDashboard != null) {
            Random random = new Random();
                        
            JsonArrayBuilder arrayBuilder;
            JsonObjectBuilder objectBuilder;
            JsonObject jsonObject;
            JsonArray jsonArray;
            KPI kpi;
            
            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("type", random.nextInt(3));
            objectBuilder.add("value", random.nextInt(100) + 100);
            jsonObject = objectBuilder.build();
            kpi = new KPI("kpi1", jsonObject.toString());
            DataGrid.put(kpi.getId(), kpi);
            
            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("value", (random.nextInt(100)));
            jsonObject = objectBuilder.build();
            kpi = new KPI("kpi2", jsonObject.toString());
            DataGrid.put(kpi.getId(), kpi);
            
            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("value", random.nextInt(100) + 100);
            jsonObject = objectBuilder.build();
            kpi = new KPI("kpi3", jsonObject.toString());
            DataGrid.put(kpi.getId(), kpi);

            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("value", random.nextInt(100) + 100);
            jsonObject = objectBuilder.build();
            kpi = new KPI("kpi4", jsonObject.toString());
            DataGrid.put(kpi.getId(), kpi);
            
            DataGrid.put(new EventKey(), new Scalar("id", random.nextInt(100)+""));
            
            
            arrayBuilder = Json.createArrayBuilder();
            
            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("name", "name1");
            objectBuilder.add("value", random.nextInt(100));
            arrayBuilder.add(objectBuilder);
            
            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("name", "name2");
            objectBuilder.add("value", random.nextInt(100));
            arrayBuilder.add(objectBuilder);

            objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("name", "name3");
            objectBuilder.add("value", random.nextInt(100));
            arrayBuilder.add(objectBuilder);
            
            jsonArray = arrayBuilder.build();
            DataGrid.put("aggregation1", new Aggregation("donut", "donut-chart", jsonArray.toString()));
                    
            
            
        }
    }

    @Lock(LockType.WRITE)
    public void start(Session session, String jsonParameters) throws Exception { 
        if (schedulerSession != null) {
            WebSocket.invokeJS(session, "simulator.alreadyStarted", "'Simulator already started by " + WebSocket.getUser(schedulerSession) + "'");
        } else {
            schedulerSession = session;
            schedulerExecCount = 0;
            JsonReader jsonReader = Json.createReader(new StringReader(jsonParameters));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();
            activeDashboard = object.getString("activeDashboard");            
            schedulerHandle = scheduler.scheduleAtFixedRate(runnable, SCHEDULER_INITIAL_DELAY, Integer.parseInt(object.getString("delay")), TimeUnit.MILLISECONDS);
            Logger.notice("Simulator started for " + activeDashboard);        
        }
    }

    @Lock(LockType.READ)
    public static Session getSchedulerSession() {
        return schedulerSession;
    }
    
    @Lock(LockType.WRITE)
    public void stop(Session session, String jsonParameters) throws Exception {  
        terminate();
    }

    @Lock(LockType.WRITE)
    public static void stop() {
        if (schedulerHandle != null)
            schedulerHandle.cancel(true);
        schedulerSession = null;
        schedulerHandle = null;
        schedulerExecCount = 0;
        activeDashboard = null;
        Logger.notice("Simulator Scheduler stoped");      
    }    

    @PostConstruct
    void initialize() {
        schedulerSession = null;
        schedulerHandle = null;
        schedulerExecCount = 0;      
        activeDashboard = null;
    }

    @PreDestroy
    public void terminate() {
        stop();
    }        
    
}
