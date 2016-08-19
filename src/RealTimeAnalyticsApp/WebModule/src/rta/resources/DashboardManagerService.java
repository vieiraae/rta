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

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.file.Files;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Set;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

import javax.ws.rs.QueryParam;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import rta.beans.DashboardManager;

import rta.datatypes.Notification;

@Path("/dashboards")
public class DashboardManagerService {    
       
    private static String DASHBOARDS_VIRTUAL_DIR_PATH = "/home/master/rta-dashboards/dashboards";
    
    @GET
    @Path("list")
    @Produces("application/json")
    public String listDirectory() {
        try {
            File dashboardsDir = new File(getVirtualDirectoryPath());
            JsonArrayBuilder jsonBuilder = listDirectory(dashboardsDir);
            JsonArray jsonArray = jsonBuilder.build();
            return jsonArray.toString();
        } catch (Exception e) {
            Logger.logException("Caught exception when adding new dashboard", e);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", "error");            
            JsonObject jsonObject = objectBuilder.build();
            return jsonObject.toString();      
        }        
    }
    
    public JsonArrayBuilder listDirectory(File directory) {    
        JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
        File[] fileList = directory.listFiles();
        Arrays.sort(fileList);
        for (File file : fileList) {
            if (file.isDirectory()) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("text", file.getName());
                JsonArrayBuilder jsonChildBuilder = listDirectory(file);
                objectBuilder.add("children", jsonChildBuilder);
                jsonBuilder.add(objectBuilder);
            } else {                
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("text", file.getName());
                objectBuilder.add("icon", "jstree-" + file.getName().substring(file.getName().lastIndexOf(".") + 1));
                jsonBuilder.add(objectBuilder);
            }
        }        
        return jsonBuilder;
    }

    @PUT
    @Path("add")
    @Produces("application/json")
    public String add(@QueryParam("source") String source, @QueryParam("target") String target) {    
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            File sourceDir = new File(getVirtualDirectoryPath() + "/" + source);
            File targetDir = new File(getVirtualDirectoryPath() + "/" + target);
            if (sourceDir.isDirectory()) {
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                    String files[] = sourceDir.list();
                    for (String file : files) {
                        File srcFile = new File(sourceDir, file);
                        File destFile = new File(targetDir, file);
                        Files.copy(srcFile.toPath(), destFile.toPath());
                        objectBuilder.add("status", "ok");
                    }                    
                } else {
                    objectBuilder.add("status", "The Directory already exists");
                }
            } else {
                objectBuilder.add("status", "The source is not a directory");
            }                        
        } catch (Exception e) {
            Logger.logException("Caught exception when adding new dashboard", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();      
    }

    @PUT
    @Path("file/rename")
    @Produces("application/json")
    public String renameFile(@QueryParam("oldName") String oldName, @QueryParam("newName") String newName) {    
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            File oldFile = new File(getVirtualDirectoryPath() + "/" + oldName);
            File newFile = new File(getVirtualDirectoryPath() + "/" + newName);
            if (newFile.exists())
                objectBuilder.add("status", "File already exists");
            else {
                objectBuilder.add("status", (oldFile.renameTo(newFile))?"ok":"Failed to rename the file");            
            }
        } catch (Exception e) {
            Logger.logException("Caught exception when adding new dashboard", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();      
    }

    @DELETE
    @Path("file/delete")
    @Produces("application/json")
    public String deleteFile(@QueryParam("file") String file) {    
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        try {
            File fileToDelete = new File(getVirtualDirectoryPath() + "/" + file);
            deleteFileOrFolder(fileToDelete);
            objectBuilder.add("status", "ok");  
        } catch (Exception e) {
            Logger.logException("Caught exception when adding new dashboard", e);
            objectBuilder.add("status", "error");            
        }
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();      
    }

    private void deleteFileOrFolder(File file) throws Exception {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    deleteFileOrFolder(f);
                }
                if (file.listFiles().length == 0)
                    file.delete();
            } else {
                file.delete();
            }
        } else throw new Exception("File does not exist");
    }
    
    @POST
    @Path("file/save")
    @Consumes("text/plain")
    @Produces("application/json")
    public String saveFile(@QueryParam("file") String file, @QueryParam("charset") String charset, String content) {    
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getVirtualDirectoryPath() + "/" + file), charset));
            writer.write(content);
            objectBuilder.add("status", "ok");            
        } catch (Exception e) {
            Logger.logException("Caught exception when saving the file", e);
            objectBuilder.add("status", "error");            
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                Logger.logException("Caught exception when closing the file", ex);
            }
        }
        
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject.toString();      
    }
        
    public String getVirtualDirectoryPath() throws Exception {
        return DASHBOARDS_VIRTUAL_DIR_PATH; 
        /* the following code only works on windows
        String virtualDirectoryPath = DashboardManager.getVirtualDirectoryPath();
        if (virtualDirectoryPath == null) {
            String virtualDirConfigFile = null;
            try {    
                virtualDirConfigFile = getClass().getResource(".").getPath() + "../../../../weblogic.xml";    
                XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(new WebAppNamespaceContext());
                String expression = "/ns:weblogic-web-app/ns:virtual-directory-mapping/ns:local-path/text()";
                InputSource inputSource = new InputSource(virtualDirConfigFile);
                virtualDirectoryPath = (String) xpath.evaluate(expression, inputSource, XPathConstants.STRING);
                virtualDirectoryPath += "/dashboards";
                DashboardManager.setVirtualDirectoryPath(virtualDirectoryPath);
            } catch(Exception e) {
                Logger.logException("Caught exception when getting the virtual directory path", e); 
                throw new Exception("Failed to get the virtual directory path");
            }
        }
        return virtualDirectoryPath;
        */
    }

    public class WebAppNamespaceContext implements NamespaceContext {
            @Override
            public String getNamespaceURI(String prefix) {
                    return "http://xmlns.oracle.com/weblogic/weblogic-web-app";
            }
            @Override
            public String getPrefix(String namespaceURI) { return null;     }
            @Override
            public Iterator getPrefixes(String namespaceURI) { return null; }
    }    
    
}