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

import java.util.UUID;

import rta.Logger;
import rta.beans.DataGrid;
import rta.datatypes.EventKey;
import rta.datatypes.JSStatement;
import rta.datatypes.Notification;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/simulator")
public class SimulatorService {


    @PUT
    @Path("jsstatement")
    @Produces("application/json")
    public String putJSStatement(@QueryParam("statement") String statement) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            DataGrid.put(new EventKey(), new JSStatement(statement));
            objectBuilder.add("status", "ok");            
        } catch (Exception e) {
            Logger.logException("Caught exception on putJSStatement", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();        
    }
    
    @PUT
    @Path("notification")
    @Produces("application/json")
    public String putNotification(@QueryParam("message") String message, @QueryParam("type") int type) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            DataGrid.put(UUID.randomUUID().toString(), new Notification(message, type));
            objectBuilder.add("status", "ok");            
        } catch (Exception e) {
            Logger.logException("Caught exception on putNotification", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();        
    }



    
}