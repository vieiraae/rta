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
package rta.beans;

import rta.Logger;
import rta.datatypes.ChatMessage;
import rta.datatypes.EventKey;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.Session;


public class Chat {

    public void sendChatMessageToAll(Session session, String jsonParameters) throws Exception {       
        if (jsonParameters.length() > 0) {
            try {
                JsonReader jsonReader = Json.createReader(new StringReader(jsonParameters));
                JsonObject object = jsonReader.readObject();
                jsonReader.close();
                ChatMessage chatMessage = new ChatMessage(object.getString("username"), object.getString("text"));
                DataGrid.put(new EventKey(), chatMessage);
            } catch (Exception e) {
                Logger.logException("Caught exception on sendChatMessageToAll", e);
            }
        }
    }    

}
