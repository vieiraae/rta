package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class Scalar implements PortableObject {
    public Scalar() {
        this.classname = this.getClass().getName();
    }

    public Scalar(String id, String data) {
        this.classname = this.getClass().getName();
        this.data = data;   
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        data = reader.readString(DATA);        
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(DATA, data);
    }
    
    public static final int CLASSNAME = 0;
    public static final int DATA = 1;
               
    
    private String classname;
    private String data;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
