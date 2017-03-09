package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.eventhandlers.ChatEventHandler;
import sai.developement.travelogue.eventhandlers.IEventHandler;

public class ChatActivity extends TravelogueActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    protected IEventHandler createEventHandler() {
        return new ChatEventHandler(this);
    }
}
