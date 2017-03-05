package sai.developement.travelogue.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sai on 2/28/17.
 */

public class User implements Parcelable{
    public String id;
    public String name;
    public String email;

    public User() {

    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.email = data[2];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {this.id,
                this.name,
                this.email});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
