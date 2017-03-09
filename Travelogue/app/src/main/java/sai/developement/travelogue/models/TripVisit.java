package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class TripVisit {

    // Will store th complete string we need to display, ex: 09:00 - 11:15
    public String tripTime;
    // Will store the place the travellers are visiting in the above time frame, ex: Taj Mahal
    public String place;
    //Will store the location- city, country the travllers are visiting.
    public String location;
    // URL to render image of the place the travllers are visiting
    public String imageUrl;
    // ID
    public String id;

    public TripVisit() {

    }

    public TripVisit(String id, String tripTime, String place, String location,String imageUrl) {
        this.id = id;
        this.tripTime = tripTime;
        this.place = place;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
