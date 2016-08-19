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
import rta.datatypes.ChatMessage;
import rta.datatypes.EventKey;
import rta.datatypes.JSStatement;
import rta.datatypes.KPI;
import rta.datatypes.Notification;
import rta.datatypes.User;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;

import com.tangosol.util.MapEvent;

import rta.datatypes.Aggregation;
import rta.datatypes.Scalar;

public class EventProcessor {

    public static void process(Set<Session> sessions, String sessionId, User user, int action) throws Exception {
        if (action == MapEvent.ENTRY_INSERTED) {
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            jsonBuilder.add("username", "system");
            jsonBuilder.add("message", "Welcome " + user.getName());
            JsonObject jsonObject = jsonBuilder.build();
            WebSocket.scheduleInvokeJS(sessions, "chat.addChatMessage", jsonObject.toString());
        }
    }

    public static void process(Set<Session> sessions, EventKey eventKey, ChatMessage chatMessage, int action) throws Exception {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("username", chatMessage.getUsername());
        jsonBuilder.add("message", chatMessage.getMessage());
        JsonObject jsonObject = jsonBuilder.build();
        WebSocket.scheduleInvokeJS(sessions, "chat.addChatMessage", jsonObject.toString());
    }
    
    public static void process(Set<Session> sessions, String eventKey, Notification notification, int action) throws Exception {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("id", eventKey);
        if (action == MapEvent.ENTRY_INSERTED || action == MapEvent.ENTRY_UPDATED) {
            Logger.notice("Broadcasting notification Message: " + notification.getMessage());
            jsonBuilder.add("message", notification.getMessage());
            jsonBuilder.add("type", notification.getType());
            JsonObject jsonObject = jsonBuilder.build();
            WebSocket.scheduleInvokeJS(sessions, "notifications.addNotification", jsonObject.toString());
        } else if (action == MapEvent.ENTRY_DELETED) {
            JsonObject jsonObject = jsonBuilder.build();
            WebSocket.scheduleInvokeJS(sessions, "notifications.removeNotification", jsonObject.toString());
        }
    }

    public static void process(Set<Session> sessions, EventKey eventKey, JSStatement statement, int action) throws Exception {
        Logger.notice("Executing JS Statement:" + statement.getStatement());
        WebSocket.scheduleInvokeJS(sessions, statement.getStatement());
    }  
    
    public static void process(Set<Session> sessions, String eventKey, KPI kpi, int action) throws Exception {
        WebSocket.scheduleInvokeJS(sessions, "dashboard.kpi.update", kpi.getId(), kpi.getData());
    }
    
    public static void process(Set<Session> sessions, EventKey eventKey, Scalar scalar, int action) throws Exception {
        WebSocket.scheduleInvokeJS(sessions, "dashboard.scalar.update", scalar.getData());
    }    
    
    public static void process(Set<Session> sessions, String eventKey, Aggregation aggregation, int action) throws Exception {
        WebSocket.scheduleInvokeJS(sessions, "dashboard.aggregation.update", aggregation.getType(), aggregation.getId(), aggregation.getData());
    }
    
}
