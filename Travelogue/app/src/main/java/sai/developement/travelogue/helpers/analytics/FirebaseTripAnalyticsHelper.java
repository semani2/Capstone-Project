package sai.developement.travelogue.helpers.analytics;

import android.os.Bundle;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseTripAnalyticsHelper {

    public static void logTripAddedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Trip.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Trip.TRIP_ADDED, bundle);
    }

    public static void logTripDeletedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Trip.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Trip.TRIP_DELETED, bundle);
    }

    public static void logTripViewedEvetnt(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Trip.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Trip.TRIP_VIEWED, bundle);
    }
}
