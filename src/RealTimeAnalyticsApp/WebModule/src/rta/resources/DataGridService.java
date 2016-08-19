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

import com.tangosol.net.Member;
import com.tangosol.net.NamedCache;

import java.text.SimpleDateFormat;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("/datagrid")
public class DataGridService {
    
    @GET
    @Path("info")
    @Produces("application/json")
    public String getInfo() {    
        JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
        
        NamedCache cache = DataGrid.getCache();
        int localMemberId = cache.getCacheService().getCluster().getLocalMember().getId();
        Set memberSet = cache.getCacheService().getCluster().getMemberSet();
        Object[] array = memberSet.toArray();
        for (int i=0; i<array.length; i++) {            
            Member member = (Member)array[i];
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("id", "Member " + member.getId());            
            objectBuilder.add("timestamp", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(member.getTimestamp()));
            objectBuilder.add("local", (member.getId() == localMemberId));
            jsonBuilder.add(objectBuilder);
        }

        JsonArray jsonArray = jsonBuilder.build();
        return jsonArray.toString();
    }
    
    @GET
    @Path("ping")
    @Produces("application/json")
    public String ping() {    
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {            
            objectBuilder.add("status", (DataGrid.getCache().isActive())?"ok":"inactive data grid");            
        } catch (Exception e) {
            Logger.logException("Caught exception on ping", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();        
    }
    
}