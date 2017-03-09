package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

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
    protected IEventHandler createEventHandler() {
        return new ViewTripEventHandler(this);
    }
}
