package sai.developement.travelogue.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sai on 2/28/17.
 */

public class User implements Parcelable {
    public String id;
    public String name;
    public String email;
    public int avatarId;

    public User() {

    }

    public User(String id, String name, String email, int avatarId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarId = avatarId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        avatarId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeInt(avatarId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}