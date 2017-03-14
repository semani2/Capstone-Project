package sai.developement.travelogue.helpers.analytics;

import android.os.Bundle;

/**
 * Created by sai on 3/14/17.
 */

public class FirebaseUserAnalyticsHelper {

    public static void logLogoutEvent() {
        Bundle bundle = FirebaseAnalyticsHelper.getFABundle();

        FirebaseAnalyticsHelper.logEvent(FirebaseAnalyticsHelper.User.LOGOUT, bundle);
    }
}
