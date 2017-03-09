package sai.developement.travelogue.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class TripMessage {

    public String message;
    public String sentTime;
    public String userId;
    public String userName;
    public int userAvatarId;

    public TripMessage() {

    }

    public TripMessage(String id, String message, String sentTime, String userId, String userName, int userAvatarId) {
        this.message = message;
        this.sentTime = sentTime;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarId = userAvatarId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAvatarId() {
        return userAvatarId;
    }

    public void setUserAvatarId(int userAvatarId) {
        this.userAvatarId = userAvatarId;
    }
}
