package rta.datatypes;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import java.util.UUID;

import java.io.IOException;

public class EventKey implements PortableObject {
    public EventKey() {
        id = UUID.randomUUID().toString();
    }

    public EventKey(String id) {
        this.id = id;
    }
    
    public void readExternal(PofReader reader) throws IOException {
        id = reader.readString(ID);
    }

    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(ID, id);
    }
    
    public static final int ID = 0;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
