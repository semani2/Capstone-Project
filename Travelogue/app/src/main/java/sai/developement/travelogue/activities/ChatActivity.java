package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import sai.developement.travelogue.R;

public class ChatActivity extends TravelogueActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAuthStateChange(@NonNull FirebaseAuth firebaseAuth) {

    }
}
