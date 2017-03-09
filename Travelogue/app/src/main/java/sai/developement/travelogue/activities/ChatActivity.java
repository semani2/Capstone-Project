package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import sai.developement.travelogue.R;
import sai.developement.travelogue.eventhandlers.ChatEventHandler;
import sai.developement.travelogue.eventhandlers.IEventHandler;
import sai.developement.travelogue.fragments.ChatFragment;
import sai.developement.travelogue.models.Trip;

public class ChatActivity extends TravelogueActivity {

    public static String TRIP_KEY = "trip_key";

    private static String CHAT_FRAGMENT_ID = "chat_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        super.onCreate(savedInstanceState);

        if(getIntent() == null || getIntent().getExtras() == null || getIntent().getExtras().getParcelable(TRIP_KEY) == null) {
            finish();
        }

        Trip trip = getIntent().getExtras().getParcelable(TRIP_KEY);

        getSupportActionBar().setTitle("Chat - " + trip.getName());

        launchChatFragment();

    }

    private void launchChatFragment() {
        if(getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_ID) == null) {
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_frame, fragment, CHAT_FRAGMENT_ID)
                    .commit();
        }
    }

    @Override
    public void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    protected IEventHandler createEventHandler() {
        return new ChatEventHandler(this);
    }
}
