package sai.developement.travelogue.helpers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.Iterator;

import sai.developement.travelogue.models.Trip;
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
    public static final String DB_NODE_SHARED_TRIPS = "shared_trips";

    private static String getCurrentUserId() {
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Logger.e("Current user null");
        return null;
    }

    public static void onLoginComplete(DatabaseReference mDatabase, User user) {
        mDatabase.child(DB_NODE_USERS).child(user.getId()).setValue(user);
    }

    /*
    Users helper methods
     */
    private static DatabaseReference getUsersDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(FirebaseDatabaseHelper.DB_NODE_USERS);
    }

    public static void getUserByEmail(String email, ValueEventListener valueEventListener) {
        DatabaseReference usersReference = getUsersDatabaseReference();
        usersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(valueEventListener);
    }

    /*
    Trips helper methods
     */

    private static DatabaseReference getTripsDatabaseReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_TRIPS);
    }

    private static DatabaseReference getSharedTripsDatabaseReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_SHARED_TRIPS);
    }

    private static DatabaseReference getTripDaysReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_TRIP_DAYS);
    }

    public static void addNewTrip(final Trip trip, final OnCompleteListener<Void> finalCompleteListener) {
        String userId = getCurrentUserId();
        if(userId != null) {
            DatabaseReference tripsReference = getTripsDatabaseReference();
            tripsReference.child(userId)
                    .child(trip.getId())
                    .setValue(trip)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                // If there are any travel mates, add the shared trips data as well
                                if(trip.getTravellers().size() > 0) {
                                    addAllSharedTrips(trip);
                                }
                                finalCompleteListener.onComplete(task);
                            }
                            else {
                                Logger.e("Error creating new trip");
                                finalCompleteListener.onComplete(task);
                            }
                        }
                    });
        }
    }

    public static void addAllSharedTrips(Trip trip) {
        Logger.d("Adding travel mates to the trip :" + trip.getName());
        Iterator<User> iterator = trip.getTravellers().iterator();

        while(iterator.hasNext()) {
            User user = iterator.next();
            getSharedTripsDatabaseReference().child(user.getId())
                    .child(trip.getId())
                    .setValue(trip);
            Logger.d("Travel mate to trip : " +trip.getName() + ", User : " +user.getEmail());
        }
    }

    public static void getItineraryForTrip(String tripId, ValueEventListener valueEventListener) {
        DatabaseReference tripDaysReference = getTripDaysReference();
        tripDaysReference.child(tripId)
                .addListenerForSingleValueEvent(valueEventListener);
    }


}
