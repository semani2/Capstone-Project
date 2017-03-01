package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class TripMessage {

    public String id;
    public String message;
    public String sentTime;

    //Stores the parent Trip ID
    public String tripId;

    public TripMessage() {

    }

    public TripMessage(String id, String message, String sentTime, String tripId) {
        this.id = id;
        this.message = message;
        this.sentTime = sentTime;
        this.tripId = tripId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
