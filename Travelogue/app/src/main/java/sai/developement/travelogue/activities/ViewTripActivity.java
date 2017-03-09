package sai.developement.travelogue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.TripPagesAdapter;
import sai.developement.travelogue.eventhandlers.IEventHandler;
import sai.developement.travelogue.eventhandlers.ViewTripEventHandler;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripDay;

public class ViewTripActivity extends TravelogueActivity {

    public static final String TRIP_KEY = "trip_key";

    private TripPagesAdapter mTripPagesAdapter;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private List<TripDay> mTripDaysList = new ArrayList<>();

    private Trip mTrip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_trip);
        ButterKnife.bind(this);

        if(getIntent().getExtras() == null || getIntent().getExtras().getParcelable(TRIP_KEY) == null) {
            // No trip object
            goHome();
        }

        setSupportActionBar(mToolbar);

        super.onCreate(savedInstanceState);

        mTrip = getIntent().getExtras().getParcelable(TRIP_KEY);

        mTripPagesAdapter = new TripPagesAdapter(mTrip, this, getSupportFragmentManager());

        mViewPager.setAdapter(mTripPagesAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.chat:
                openChat();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openChat() {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(ChatActivity.TRIP_KEY, mTrip);

        chatIntent.putExtras(extras);
        startActivity(chatIntent);
    }

    @Override
    protected IEventHandler createEventHandler() {
        return new ViewTripEventHandler(this);
    }
}
