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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        ButterKnife.bind(this);

        launchAddNewTripFragment();
    }

    private void launchAddNewTripFragment() {
        AddNewTripFragment fragment = new AddNewTripFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.new_trip_fragment_container, fragment)
                .commit();
    }
}
