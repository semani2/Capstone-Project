package sai.developement.travelogue.helpers.analytics;

import android.os.Bundle;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseTravelMateAnalyticsHelper {

    public static void logTravelMateAddedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.TravelMates.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.TravelMates.TRAVEL_MATE_ADDED, bundle);
    }

    public static void logTravelMateRemovedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.TravelMates.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.TravelMates.TRAVEL_MATE_REMOVED, bundle);
    }
}
