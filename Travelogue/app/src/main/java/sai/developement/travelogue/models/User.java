package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class User {
    public String id;
    public String name;
    public String profileImageUrl;

    public User() {

    }

    public User(String id, String name, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
