package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class User implements PortableObject {
    public User() {
        this.classname = this.getClass().getName();
    }

    public User(String name) {
        this.classname = this.getClass().getName();
        this.name = name;
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        name = reader.readString(NAME);
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(NAME, name);
    }
    
    public static final int CLASSNAME = 0;
    public static final int NAME = 1;
           
    
    private String classname;
    private String name;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
