package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.adapters.TripMatesListAdapter;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripDetailsFragment extends Fragment {
    @BindView(R.id.trip_name_text_view)
    TextView tripNameTextView;

    @BindView(R.id.start_date_text_view)
    TextView startDateTextView;

    @BindView(R.id.end_date_text_view)
    TextView endDateTextView;

    @BindView(R.id.save_trip_button)
    Button saveTripButton;

    @BindView(R.id.add_user_button)
    Button addUserButton;

    @BindView(R.id.user_emaiL_edit_text)
    EditText emailEditText;

    @BindView(R.id.trip_mates_list_view)
    ListView tripmatesListView;

    @BindView(R.id.progress_bar_layout)
    RelativeLayout progressBarLayout;

    @BindView(R.id.users_list_empty_view)
    TextView emptyListView;

    private Trip mTrip;

    private TripMatesListAdapter mTripMatesListAdapter;

    private ArrayList<User> mTripMates = new ArrayList<>();

    public TripDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().getParcelable(ViewTripActivity.TRIP_KEY) != null) {
            mTrip = getArguments().getParcelable(ViewTripActivity.TRIP_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
        ButterKnife.bind(this, view);

        if(mTrip != null) {
            tripNameTextView.setText(mTrip.getName());
            startDateTextView.setText(mTrip.getStartDate());
            endDateTextView.setText(mTrip.getEndDate());
            if(mTrip.getTravellers() != null) {
                mTripMates = new ArrayList<>(mTrip.getTravellers());
            }

            mTripMatesListAdapter = new TripMatesListAdapter(getContext(), mTripMates);
            tripmatesListView.setAdapter(mTripMatesListAdapter);
            tripmatesListView.setEmptyView(emptyListView);
        }
        else {
            // We should really not be here!! :(
        }

        return view;
    }

}
