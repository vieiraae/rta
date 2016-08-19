package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class Aggregation implements PortableObject {
    public Aggregation() {
        this.classname = this.getClass().getName();
    }

    public Aggregation(String type, String id, String data) {
        this.classname = this.getClass().getName();
        this.type = type;
        this.id = id;
        this.data = data;   
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        type = reader.readString(TYPE);
        id = reader.readString(ID);
        data = reader.readString(DATA);        
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(TYPE, type);
        writer.writeString(ID, id);
        writer.writeString(DATA, data);
    }
    
    public static final int CLASSNAME = 0;
    public static final int TYPE = 1;
    public static final int ID = 2;
    public static final int DATA = 3;
               
    
    private String classname;
    private String type;
    private String id;
    private String data;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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
