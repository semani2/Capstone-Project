package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.TripPagesAdapter;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripDay;

public class ViewTripActivity extends AppCompatActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        ButterKnife.bind(this);

        if(getIntent().getExtras() == null || getIntent().getExtras().getParcelable(TRIP_KEY) == null) {
            // No trip object
            finish();
        }

        setSupportActionBar(mToolbar);


        mTrip = getIntent().getExtras().getParcelable(TRIP_KEY);

        mTripPagesAdapter = new TripPagesAdapter(mTrip, mTripDaysList, this, getSupportFragmentManager());

        mViewPager.setAdapter(mTripPagesAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        FirebaseDatabaseHelper.getItineraryForTrip(mTrip.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && dataSnapshot.getValue()!= null) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
