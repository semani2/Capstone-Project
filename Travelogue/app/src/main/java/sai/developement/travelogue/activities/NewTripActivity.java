package sai.developement.travelogue.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.fragments.AddNewTripFragment;

public class NewTripActivity extends AppCompatActivity {

    @BindView(R.id.new_trip_fragment_container)
    FrameLayout fragmentContainer;

    private static final String NEW_TRIP_FRAGENT_ID = "new_trip_fragment_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        ButterKnife.bind(this);

        launchAddNewTripFragment();
    }

    private void launchAddNewTripFragment() {
        if(getSupportFragmentManager().findFragmentByTag(NEW_TRIP_FRAGENT_ID) == null) {
            AddNewTripFragment fragment = new AddNewTripFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.new_trip_fragment_container, fragment, NEW_TRIP_FRAGENT_ID)
                    .commit();
        }
    }
}
