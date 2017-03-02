package sai.developement.travelogue.helpers;

import com.google.firebase.database.DatabaseReference;

import sai.developement.travelogue.models.User;

/**
 * Created by sai on 3/2/17.
 */

public class FirebaseDatabaseHelper {

    public static final String DB_NODE_USERS = "users";
    public static final String DB_NODE_TRIPS = "trips";
    public static final String DB_NODE_TRIP_DAYS = "trip_days";
    public static final String DB_NODE_TRIP_VISITS = "trip_visits";
    public static final String DB_NODE_MESSAGES = "messages";

    public static void onLoginComplete(DatabaseReference mDatabase, User user) {
        mDatabase.child(DB_NODE_USERS).child(user.getId()).setValue(user);
    }

}
