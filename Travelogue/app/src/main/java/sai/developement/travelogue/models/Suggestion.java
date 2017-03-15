package sai.developement.travelogue.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sai on 3/7/17.
 */

public class Suggestion implements Parcelable {

    private String id;

    private String name;

    private String photoUrl;

    private String address;

    public Suggestion() {

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    protected Suggestion(Parcel in) {
        id = in.readString();
        name = in.readString();
        photoUrl = in.readString();
        address = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photoUrl);
        dest.writeString(address);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Suggestion> CREATOR = new Parcelable.Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };
}
