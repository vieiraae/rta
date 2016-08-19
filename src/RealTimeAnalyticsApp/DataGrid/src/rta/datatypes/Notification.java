package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class Notification implements PortableObject, Comparable<Notification> {
    public Notification() {
        this.classname = this.getClass().getName();
    }

    public Notification(String message, int type) {
        this.classname = this.getClass().getName();
        this.message = message;
        this.timestamp = Calendar.getInstance().getTimeInMillis();        
        this.type = type;
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        message = reader.readString(MESSAGE);
        timestamp = reader.readLong(TIMESTAMP);
        type = reader.readInt(TYPE);        
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(MESSAGE, message);
        writer.writeLong(TIMESTAMP, timestamp);
        writer.writeInt(TYPE, type);        
    }
    
    public int compareTo(Notification other) {
        if (this.timestamp < other.timestamp) {
            return -1;
        }
        if (this.timestamp > other.timestamp) {
            return 1;
        }
        return 0;
    }    
    
    public static final int CLASSNAME = 0;
    public static final int MESSAGE = 1;
    public static final int TIMESTAMP = 2;
    public static final int TYPE = 3;
    
    
    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int CRITICAL = 3;
    public static final int FATAL = 4;
    
    
    private String classname;
    private String message;
    private long timestamp;
    private int type;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }    

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
