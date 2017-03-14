package sai.developement.travelogue.helpers.analytics;

import android.os.Bundle;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseChatAnalyticsHelper {
    public static void logChatOpenedEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Chat.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Chat.CHAT_OPENED, bundle);
    }

    public static void logChatsgSentEvent(String tripId) {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();
        bundle.putString(FirebaseAnalyticsHelper.Chat.Params.TRIP_ID, tripId);

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.Chat.CHAT_MSG_SENT, bundle);
    }
}
