package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class TripDay {

    public String id;
    public String date;


    public TripDay() {

    }

    public TripDay(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
