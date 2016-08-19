package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import java.util.Calendar;

public class ChatMessage implements PortableObject {
    public ChatMessage() {
        this.classname = this.getClass().getName();
    }

    public ChatMessage(String username, String message) {
        this.classname = this.getClass().getName();
        this.username = username;
        this.message = message;
        this.timestamp = Calendar.getInstance().getTimeInMillis();        
    }
       
    public void readExternal(PofReader reader) throws IOException {
        classname = reader.readString(CLASSNAME);
        username = reader.readString(USERNAME);
        message = reader.readString(MESSAGE);
        timestamp = reader.readLong(TIMESTAMP);
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(CLASSNAME, classname);
        writer.writeString(USERNAME, username);
        writer.writeString(MESSAGE, message);
        writer.writeLong(TIMESTAMP, timestamp);
    }
    
    public static final int CLASSNAME = 0;
    public static final int USERNAME = 1;
    public static final int MESSAGE = 2;
    public static final int TIMESTAMP = 3;
    private String classname;
    private String username;
    private String message;
    private long timestamp;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
