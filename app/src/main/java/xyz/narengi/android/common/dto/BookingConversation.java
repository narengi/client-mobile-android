package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class BookingConversation implements Serializable {

    private boolean FromHost;
    private String Message;
    private String Time;


    public boolean isFromHost() {
        return FromHost;
    }

    public void setFromHost(boolean fromHost) {
        FromHost = fromHost;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
