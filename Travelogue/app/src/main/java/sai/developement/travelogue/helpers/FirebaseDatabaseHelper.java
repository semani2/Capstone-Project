package sai.developement.travelogue.helpers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.events.SelectAvatarEvent;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripDay;
import sai.developement.travelogue.models.TripVisit;
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

    private static Context sContext;

    private static String getCurrentUserId() {
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Logger.e("Current user null");
        return null;
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static void onLoginComplete(DatabaseReference mDatabase, final User user) {
        //Check if user is already in the db if yes, leave him alone and check for avatar, else add the user
        Logger.d("Login Complete for User: " + user.getName());
        final DatabaseReference usersDatabaseReference = getUsersDatabaseReference();
        usersDatabaseReference.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    // User already exists
                    Logger.d("User " + user.getName() + " already exists in the DB");
                    checkUserHasAvatar(user);
                } else {
                    usersDatabaseReference.child(user.getId()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Check is user has avatar if not send out AvatarSelectEvent
                                        checkUserHasAvatar(user);
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e("Checking for user in Firebase cancelled " + databaseError.getMessage());
            }
        });
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

    private static void checkUserHasAvatar(final User user) {
        DatabaseReference userReference = getUsersDatabaseReference();
        userReference.child(user.getId()).child("avatarId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Logger.d("Checking for users avatar");
                setCurrentUser(user);
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    if(0 == (Long) dataSnapshot.getValue()) {
                        EventBus.getDefault().post(new SelectAvatarEvent(user.getId()));
                        Logger.d("User does not have avatar, throwing avatar select event");
                    }
                    else {
                        Logger.d("User already has avatar");
                        CurrentUser.setCurrentUserAvatarId(((Long) dataSnapshot.getValue()).intValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e("Checking for user's avatar cancelled " + databaseError.getMessage());
            }
        });
    }

    private static void setCurrentUser(User user) {
        CurrentUser.setCurrentUser(sContext, user.getId(), user.getName(), user.getEmail());
    }

    public static void saveUserAvatar(String userId, final int avatarId, final OnCompleteListener onCompleteListener) {
        DatabaseReference userReference = getUsersDatabaseReference();
        userReference.child(userId)
                .child("avatarId")
                .setValue(avatarId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            CurrentUser.setCurrentUserAvatarId(avatarId);
                        }
                        onCompleteListener.onComplete(task);
                    }
                });
    }

    /*
    Trips helper methods
     */

    public static DatabaseReference getTripsDatabaseReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_TRIPS);
    }

    private static DatabaseReference getSharedTripsDatabaseReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_SHARED_TRIPS);
    }

    public static DatabaseReference getTripDaysReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDatabaseHelper.DB_NODE_TRIP_DAYS);
    }

    public static DatabaseReference getTripVisitsReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(DB_NODE_TRIP_VISITS);
    }

    public static DatabaseReference getSharedTripsReference() {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(DB_NODE_SHARED_TRIPS);
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

        //Before adding the shared trips add the current user as part of the travel mates, so that the the travel mates know
        ArrayList<User> travelMates = new ArrayList<>(trip.getTravellers());
        CurrentUser currentUser = CurrentUser.getCurrentuser();
        User cUser = new User();
        cUser.setName(currentUser.getUserName());
        cUser.setEmail(currentUser.getUserEmail());
        cUser.setId(currentUser.getUserId());
        cUser.setAvatarId(currentUser.getUserAvatarId());
        travelMates.add(cUser);

        trip.setTravellers(travelMates);

        Iterator<User> iterator = trip.getTravellers().iterator();

        while(iterator.hasNext()) {
            User user = iterator.next();
            if(user.getId().equalsIgnoreCase(cUser.getId())) {
                continue;
            }
            getSharedTripsDatabaseReference().child(user.getId())
                    .child(trip.getId())
                    .setValue(trip);
            Logger.d("Travel mate to trip : " +trip.getName() + ", User : " +user.getEmail());
        }
    }

    public static void addSharedTrip(String userId, Trip trip) {
        Logger.d("Adding a shared trip to the user");

        getSharedTripsDatabaseReference().child(userId)
                .child(trip.getId())
                .setValue(trip);
    }

    public static void deleteTrip(Trip trip) {
        Logger.d("Deleting Trip " + trip.getId());

        List<User> travelMates = trip.getTravellers();

        DatabaseReference tripDatabaseReference = getTripsDatabaseReference();
        tripDatabaseReference.child(CurrentUser.getCurrentuser().getUserId())
                .child(trip.getId())
                .removeValue();

        DatabaseReference sharedTripsDatabaseReference = getSharedTripsDatabaseReference();
        // Remove all shared trips

        if(travelMates != null && travelMates.size() > 0) {
            for (User user : travelMates) {
                sharedTripsDatabaseReference.child(user.getId())
                        .child(trip.getId())
                        .removeValue();
            }
        }
    }

    public static void addTripDayForTrip(Trip trip, TripDay tripDay) {
        Logger.d("Adding trip day " + tripDay.getId() + " , for trip : " + trip.getId());

        DatabaseReference tripDayReference = getTripDaysReference();
        tripDayReference.child(trip.getId())
                .child(tripDay.getId())
                .setValue(tripDay);
    }

    public static void getTripDay(String tripId, String date, ValueEventListener valueEventListener) {
        Logger.d("Fetching Trip day on date : "+ date+ " , for trip :" + tripId);

        DatabaseReference tripDaysReference = getTripDaysReference();
        tripDaysReference.child(tripId)
                .orderByChild("date")
                .equalTo(date)
                .addListenerForSingleValueEvent(valueEventListener);

    }

    public static void getItineraryForTrip(String tripId, ValueEventListener valueEventListener) {
        DatabaseReference tripDaysReference = getTripDaysReference();
        tripDaysReference.child(tripId)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public static void addTripVisitToDay(String tripDayId, TripVisit tripVisit) {
        DatabaseReference databaseReference = getTripVisitsReference();
        databaseReference.child(tripDayId)
                .child(tripVisit.getId())
                .setValue(tripVisit);
    }

    /* Chat Helper methods */
    public static DatabaseReference getChatDatabaseReference(String tripId) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(DB_NODE_MESSAGES)
                .child(tripId);
    }

}
