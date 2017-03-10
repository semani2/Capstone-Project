package sai.developement.travelogue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.HomePagesAdapter;
import sai.developement.travelogue.eventhandlers.HomeEventHandler;
import sai.developement.travelogue.eventhandlers.IEventHandler;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.User;

public class HomeActivity extends TravelogueActivity {

    private DatabaseReference mDatabaseReference;

    public static final int MY_TRIPS_FLAG = 0;
    public static final int SHARED_TRIPS_FLAG = 1;

    public static final String TRIP_FLAG = "trip";
    public static final String USER_ID_KEY = "user_id_key";

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private HomePagesAdapter mHomePagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // Launch New Trip activity to create a new trip
                Intent newTripIntent = new Intent(HomeActivity.this, NewTripActivity.class);
                startActivity(newTripIntent);
            }
        });

        setSupportActionBar(mToolbar);
    }

    @Override
    protected IEventHandler createEventHandler() {
        return new HomeEventHandler(this);
    }

    @Override
    public void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // Add the user to Firebase if not yet added
        User user = new User();
        user.setId(currentUser.getUid());
        user.setName(currentUser.getDisplayName());
        user.setEmail(currentUser.getEmail());

        FirebaseDatabaseHelper.onLoginComplete(mDatabaseReference, user);

        if(mHomePagesAdapter == null) {
            mHomePagesAdapter = new HomePagesAdapter(getSupportFragmentManager(), this, user.getId());

            mViewPager.setAdapter(mHomePagesAdapter);

            mTabLayout.setupWithViewPager(mViewPager);
        }
    }
}
