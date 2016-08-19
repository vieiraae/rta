package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class KPI implements PortableObject {
    public KPI() {
        this.classname = this.getClass().getName();
    }

    public KPI(String id, String data) {
        this.classname = this.getClass().getName();
        this.id = id;
        this.data = data;   
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        id = reader.readString(ID);
        data = reader.readString(DATA);        
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(ID, id);
        writer.writeString(DATA, data);
    }
    
    public static final int CLASSNAME = 0;
    public static final int ID = 1;
    public static final int DATA = 2;
               
    
    private String classname;
    private String id;
    private String data;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
