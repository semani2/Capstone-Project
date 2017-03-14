package sai.developement.travelogue;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import sai.developement.travelogue.helpers.analytics.FirebaseAnalyticsHelper;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;

/**
 * Created by sai on 3/8/17.
 */

public class TravelogueApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabaseHelper.setContext(this);
        FirebaseAnalyticsHelper.setAnalyticsInstance(this);
    }
}
