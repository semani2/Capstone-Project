package sai.developement.travelogue.helpers.analytics;

import android.os.Bundle;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseItineraryAnalyticsHelper {

    public static void logItineraryAddedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Itinerary.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Itinerary.ITINERARY_ADDED, bundle);
    }

    public static void logItineraryDeletedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Itinerary.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Itinerary.ITINERARY_DELETED, bundle);
    }
}
