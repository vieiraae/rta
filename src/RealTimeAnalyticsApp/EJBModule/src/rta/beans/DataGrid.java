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

import rta.datatypes.EventKey;
import rta.datatypes.ChatMessage;
import rta.datatypes.JSStatement;
import rta.datatypes.KPI;
import rta.datatypes.Notification;
import rta.datatypes.User;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MultiplexingMapListener;
import com.tangosol.util.Filter;
import com.tangosol.util.filter.AndFilter;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.MapEventFilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.CloseReason;
import javax.websocket.Session;

import rta.datatypes.Aggregation;
import rta.datatypes.Scalar;

@Startup
@Singleton
public class DataGrid {
    
    public class UserListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof String && event.getNewValue() instanceof User)
                    EventProcessor.process(sessions, (String)event.getKey(), (User) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on UserListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the User event", e);
            }
        }
    }        
    public class ChatMessageListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof EventKey && event.getNewValue() instanceof ChatMessage)
                    EventProcessor.process(sessions, (EventKey)event.getKey(), (ChatMessage) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on ChatMessageListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the ChatMessage event", e);
            }
        }
    }    
    public class NotificationListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof String && (event.getOldValue() instanceof Notification || event.getNewValue() instanceof Notification))
                    EventProcessor.process(sessions, (String)event.getKey(), (Notification) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on NotificationListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the Notification event", e);
            }
        }
    }        
    public class JSStatementListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof EventKey && event.getNewValue() instanceof JSStatement)
                    EventProcessor.process(sessions, (EventKey)event.getKey(), (JSStatement) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on JSStatementListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the JSStatement event", e);
            }
        }
    }        
    public class KPIListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof String && event.getNewValue() instanceof KPI)
                    EventProcessor.process(sessions, (String)event.getKey(), (KPI) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on KPIListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the KPI event", e);
            }
        }
    }
    public class ScalarListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof EventKey && event.getNewValue() instanceof Scalar)
                    EventProcessor.process(sessions, (EventKey)event.getKey(), (Scalar) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on ScalarListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the Notification event", e);
            }
        }
    }        
    public class AggregationListener extends MultiplexingMapListener {
        public void onMapEvent(MapEvent event) {
            try {       
                if (event.getKey() instanceof String && event.getNewValue() instanceof Aggregation)
                    EventProcessor.process(sessions, (String)event.getKey(), (Aggregation) event.getNewValue(), event.getId());                    
                else
                    Logger.error("Unexpected Event on AggregationListener!");
            } catch (Exception e) {
                Logger.logException("Caught exception when processing the KPI event", e);
            }
        }
    }
    
    UserListener userListener;
    ChatMessageListener chatMessageListener;
    NotificationListener notificationListener;
    JSStatementListener jsStatementListener;
    KPIListener kpiListener;
    ScalarListener scalarListener;
    AggregationListener aggregationListener;

    private static NamedCache cache;
    
    private static Set<Session> sessions; 
    
    @Lock(LockType.WRITE)
    public static void put(Object key, Object value) throws Exception {
        cache.put(key, value);
    }

    @Lock(LockType.READ)
    public static Object get(Object key) throws Exception {
        return cache.get(key);
    }

    @Lock(LockType.WRITE)
    public static Object remove(Object key) throws Exception {
        return cache.remove(key);
    }

    public static NamedCache getCache() {
        return cache;
    }
    
    @Lock(LockType.WRITE)
    public static void addSession(final Session session) throws Exception {
        Logger.notice("Adding session");
        sessions.add(session);
        cache.put(session.getId(), new User(WebSocket.getUser(session)));
    }
    
    @Lock(LockType.WRITE)
    public static void removeSession(final Session session) throws Exception {
        Logger.notice("Removing session");
        cache.remove(session.getId());
        sessions.remove(session);
    }    
    
    public static Set<Session> getSessions() {
        return sessions;
    }
    
    @PostConstruct
    void initialize() {
        Logger.notice("Creating the named cache");
        
        try {
            cache  = CacheFactory.getCache("rta");
            sessions = Collections.synchronizedSet(new HashSet<Session>()); 
            
            userListener = new UserListener();
            chatMessageListener = new ChatMessageListener();
            notificationListener = new NotificationListener();
            jsStatementListener = new JSStatementListener();
            kpiListener = new KPIListener();
            scalarListener = new ScalarListener();
            scalarListener = new ScalarListener();
            aggregationListener = new AggregationListener();
            
            int insertedUpdatedMask = MapEventFilter.E_INSERTED | MapEventFilter.E_UPDATED;
            int insertedUpdatedDeletedMask = MapEventFilter.E_INSERTED | MapEventFilter.E_UPDATED | MapEventFilter.E_DELETED;
            
            Filter filterUser = new EqualsFilter("getClassname", User.class.getName()); 
            Filter filterChatMessage = new EqualsFilter("getClassname", ChatMessage.class.getName());           
            Filter filterNotification = new EqualsFilter("getClassname", Notification.class.getName());           
            Filter filterJSStatement = new EqualsFilter("getClassname", JSStatement.class.getName());
            Filter filterKPI = new EqualsFilter("getClassname", KPI.class.getName());
            Filter filterScalar = new EqualsFilter("getClassname", Scalar.class.getName());
            Filter filterAggregation = new EqualsFilter("getClassname", Aggregation.class.getName());
            
            cache.addMapListener(userListener, new MapEventFilter(insertedUpdatedMask, filterUser), false);       
            cache.addMapListener(chatMessageListener, new MapEventFilter(insertedUpdatedMask, filterChatMessage), false);       
            cache.addMapListener(notificationListener, new MapEventFilter(insertedUpdatedDeletedMask, filterNotification), false);            
            cache.addMapListener(jsStatementListener, new MapEventFilter(insertedUpdatedMask, filterJSStatement), false);            
            cache.addMapListener(kpiListener, new MapEventFilter(insertedUpdatedMask, filterKPI), false);    
            cache.addMapListener(scalarListener, new MapEventFilter(insertedUpdatedMask, filterScalar), false);    
            cache.addMapListener(aggregationListener, new MapEventFilter(insertedUpdatedMask, filterAggregation), false);    
        } catch (Exception e) {
            Logger.logException("Caught exception when adding the map listener", e);
        }                   
    }    
    
    @PreDestroy
    public void terminate() {
        Logger.notice("Terminating DataGrid Singleton");
        try {
            sessions.clear();
            cache.removeMapListener(userListener);       
            cache.removeMapListener(chatMessageListener);       
            cache.removeMapListener(notificationListener);       
            cache.removeMapListener(jsStatementListener);       
            cache.removeMapListener(kpiListener);       
            cache.removeMapListener(scalarListener);       
            cache.removeMapListener(aggregationListener);       
            Filter filterUser = new EqualsFilter("getClassname", User.class.getName());             
            for (Iterator iter = cache.entrySet(filterUser).iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iter.next();
                cache.remove(entry.getKey());
            }
        } catch (Exception e) {
            Logger.logException("Caught exception when clearing the cache", e);
        }            
    }    
    
}
