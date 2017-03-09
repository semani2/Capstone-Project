package sai.developement.travelogue.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sai on 2/28/17.
 */

@IgnoreExtraProperties
public class Trip implements Parcelable {

    public String id;
    public String name;
    public String startDate;
    public String endDate;
    public List<User> travellers;
    public int duration;
    public String createdByUsername;
    public String createByUseremail;
    public long startDateMillis;


    public Trip() {
        // Default constructor for Firebase
    }

    public Trip(String id, String name, String startDate, String endDate, List<User> travellers, int duration,
                String createdByUsername, String createByUseremail, long startDateMillis) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travellers = travellers;
        this.duration = duration;
        this.createdByUsername = createdByUsername;
        this.createByUseremail = createByUseremail;
        this.startDateMillis = startDateMillis;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    public long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    protected Trip(Parcel in) {
        id = in.readString();
        name = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        if (in.readByte() == 0x01) {
            travellers = new ArrayList<User>();
            in.readList(travellers, User.class.getClassLoader());
        } else {
            travellers = null;
        }
        duration = in.readInt();
        createdByUsername = in.readString();
        createByUseremail = in.readString();
        startDateMillis = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        if (travellers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(travellers);
        }
        dest.writeInt(duration);
        dest.writeString(createdByUsername);
        dest.writeString(createByUseremail);
        dest.writeLong(startDateMillis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}