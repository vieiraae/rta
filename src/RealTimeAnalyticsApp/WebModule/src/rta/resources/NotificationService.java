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
package rta.resources;

import rta.Logger;
import rta.beans.DataGrid;

import com.tangosol.util.Filter;
import com.tangosol.util.filter.EqualsFilter;

import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

import javax.ws.rs.QueryParam;

import rta.datatypes.EventKey;
import rta.datatypes.Notification;

@Path("/notification")
public class NotificationService {
    
    @GET
    @Path("all")
    @Produces("application/json")
    public String getAllNotifications() {    
        JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
        Filter filter = new EqualsFilter("getClassname", Notification.class.getName()); 
        for (Iterator iter = DataGrid.getCache().entrySet(filter, null).iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iter.next();            
            Notification notification = (Notification)entry.getValue();
            String eventKey = (String)entry.getKey();
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("id", eventKey);
            objectBuilder.add("message", notification.getMessage());
            objectBuilder.add("type", notification.getType());
            jsonBuilder.add(objectBuilder);
        }
        JsonArray jsonArray = jsonBuilder.build();
        return jsonArray.toString();

    }
    
    @DELETE
    @Produces("application/json")
    public String remvoveNotification(@QueryParam("id") String id) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            Object obj = DataGrid.remove(id);
            objectBuilder.add("status", (obj != null)?"ok":"notification not found");            
        } catch (Exception e) {
            Logger.logException("Caught exception on remvoveNotification", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();        
    }    
}