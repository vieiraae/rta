package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class JSStatement implements PortableObject {
    public JSStatement() {
        this.classname = this.getClass().getName();
    }

    public JSStatement(String statement) {
        this.classname = this.getClass().getName();
        this.statement = statement;
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        statement = reader.readString(STATEMENT);
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(STATEMENT, statement);
    }
    
    public static final int CLASSNAME = 0;
    public static final int STATEMENT = 1;
           
    
    private String classname;
    private String statement;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

}
