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
package rta.websocket;

import rta.Logger;
import rta.beans.DataGrid;
import rta.beans.WebSocket;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnError;
import javax.json.Json;
import javax.json.JsonObject;

import rta.beans.Simulator;

@ServerEndpoint("/stream")
public class Endpoint {

    private static boolean ALLOW_USER_MULTI_CONNECTIONS = true;
        
    @OnOpen
    public void onOpen(Session session, EndpointConfig conf){
        try {
            session.setMaxIdleTimeout(0);
            if (session.getUserPrincipal() == null || session.getUserPrincipal().getName().length() == 0) {
                Logger.notice("new session -> redirecting to the login screen");
                session.getBasicRemote().sendText("location.href = './login.jsp'");
            } else {
                String user = WebSocket.getUser(session);
                if (!ALLOW_USER_MULTI_CONNECTIONS) {
                    int userCount = 0;
                    for  (Session sess : session.getOpenSessions()) {
                        if (user.equalsIgnoreCase(WebSocket.getUser(sess)))
                            userCount++;
                    }
                    if (userCount > 1) {
                        Logger.notice("connection not allowed because user is already connected");
                        session.getBasicRemote().sendText("location.href = './login.jsp?action=reject'");  
                        return;
                    }
                }
                Logger.notice("connection opened for " + user + ". # of clients connected: " + session.getOpenSessions().size());
                JsonObject jsonObject = Json.createObjectBuilder().add("username", user).build();
                WebSocket.invokeJS(session, "rta.setUser", jsonObject.toString());
                DataGrid.addSession(session);
            }
        } catch (Exception e){
            Logger.logException("caught exception in method onOpen", e);
        }
    }

    @OnMessage
    public void onMessage(Session session, String data) {
        try {
            Logger.notice("message received: " + data);
            Pattern pattern = Pattern.compile("(.*)\\.(.*)\\((.*)\\);$");
            Matcher matcher = pattern.matcher(data);
            
            if (matcher.matches()) {
                Class<?> handlerClass = Class.forName(matcher.group(1));
                Object handler = handlerClass.newInstance();                
                Class<?>[] paramTypes = new Class[2];
                paramTypes[0] = Session.class;
                paramTypes[1] = String.class;      
                Method method = handler.getClass().getMethod(matcher.group(2), paramTypes);
                method.invoke(handler, session, matcher.group(3).trim());                                    
            }
        } catch (Exception e){
            Logger.logException("caught exception in method onMessage", e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason message) {        
        try {
            DataGrid.removeSession(session);            
            if (Simulator.getSchedulerSession() != null && session != null) {
                if (Simulator.getSchedulerSession().getId().equals(session.getId())) {
                    Simulator.stop();
                }                
            }
            Logger.notice("connection closed");
        } catch (Exception e) {
            Logger.error("caught exception in method onClose");
        }     
    }

    @OnError
    public void onError(Throwable t) {
        Logger.error("onError:" + t.getMessage());
        t.printStackTrace();
    }
    
}


