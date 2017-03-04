package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class Trip {

    public String id;
    public String name;
    public String startDate;
    public String endDate;
    public List<User> travellers;
    public Long duration;
    public String createdByUsername;
    public String createByUseremail;


    public Trip() {
        // Default constructor for Firebase
    }

    public Trip(String id, String name, String startDate, String endDate, List<User> travellers, Long duration,
                String createdByUsername, String createByUseremail) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travellers = travellers;
        this.duration = duration;
        this.createdByUsername = createdByUsername;
        this.createByUseremail = createByUseremail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<User> getTravellers() {
        return travellers;
    }

    public void setTravellers(List<User> travellers) {
        this.travellers = travellers;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public String getCreateByUseremail() {
        return createByUseremail;
    }

    public void setCreateByUseremail(String createByUseremail) {
        this.createByUseremail = createByUseremail;
    }
}
