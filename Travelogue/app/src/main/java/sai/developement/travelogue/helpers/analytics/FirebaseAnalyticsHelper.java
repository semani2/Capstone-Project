package sai.developement.travelogue.helpers.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import sai.developement.travelogue.CurrentUser;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseAnalyticsHelper {

    private static FirebaseAnalytics sFirebaseAnalytics;

    private static final String USER_ID = "user_id";

    public static void setAnalyticsInstance(Context context) {
        sFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void logEvent(String event, Bundle eventParams) {
        sFirebaseAnalytics .logEvent(event, eventParams);
    }

    public static Bundle getFABundle() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, CurrentUser.getCurrentuser().getUserId());

        return bundle;
    }

    public static class Trip {
        public static final String TRIP_ADDED = "trip_added";

        public static final String TRIP_DELETED = "trip_deleted";

        public static final String TRIP_VIEWED = "trip_viewed";

        public static class Params {
            public static final String TRIP_ID = "trip_id";
        }
    }

    public static class TravelMates {
        public static final String TRAVEL_MATE_ADDED = "travel_mate_added";

        public static final String TRAVEL_MATE_REMOVED = "travel_mate_removed";

        public static class Params {
            public static final String TRIP_ID = "trip_id";
        }
    }

    public static class Itinerary {
        public static final String ITINERARY_ADDED = "itinerary_added";

        public static final String ITINERARY_DELETED = "itinerary_deleted";

        public static class Params {
            public static final String TRIP_ID = "trip_id";
        }
    }

    public static class Chat {
        public static final String CHAT_OPENED = "chat_opened";

        public static final String CHAT_MSG_SENT = "chat_message_sent";

        public static class Params {
            public static final String TRIP_ID = "trip_id";
        }
    }

    public static class User {
        public static final String LOGOUT = "logout";
    }
}
