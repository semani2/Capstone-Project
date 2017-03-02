package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class TripMessage {

    public String message;
    public String sentTime;
    public String userName;

    public TripMessage() {

    }

    public TripMessage(String id, String message, String sentTime, String userName) {
        this.message = message;
        this.sentTime = sentTime;
        this.userName = userName;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }
}
