package sai.developement.travelogue.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.adapters.TripsRecyclerAdapter;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/10/17.
 */

public class TripsFragment extends Fragment {

    @BindView(R.id.trips_recycler_view)
    RecyclerView tripsRecyclerView;

    @BindView(R.id.emptyRecyclerTextView)
    TextView emptyTripsTextView;

    private RecyclerView.LayoutManager mLayoutManager;
    private TripsRecyclerAdapter mTripsAdapter;
    private ArrayList<Trip> mTrips = new ArrayList<>();

    private ChildEventListener mTripsEventListener;
    private DatabaseReference mTripsReference;

    private String mUserId;

    private int mTripsFlag = 0;

    public TripsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();

        mUserId = getArguments().getString(HomeActivity.USER_ID_KEY);

        mTripsFlag = getArguments().getInt(HomeActivity.TRIP_FLAG);
        if(mTripsFlag == HomeActivity.MY_TRIPS_FLAG) {
            mTripsReference = FirebaseDatabaseHelper.getTripsDatabaseReference().
                    child(mUserId);
        }
        else {
            mTripsReference = FirebaseDatabaseHelper.getSharedTripsReference().
                    child(mUserId);
        }

        initEventListener();
        return view;
    }


    private void initEventListener() {
        mTripsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    addDataToAdapter(dataSnapshot.getValue(Trip.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initRecyclerView() {
        tripsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        tripsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mTripsAdapter = new TripsRecyclerAdapter(getContext(), mTrips, getActivity(), mTripsFlag);
        tripsRecyclerView.setAdapter(mTripsAdapter);

        if(mTrips.size() == 0) {
            tripsRecyclerView.setVisibility(View.GONE);
            emptyTripsTextView.setVisibility(View.VISIBLE);
        }
        else {
            tripsRecyclerView.setVisibility(View.VISIBLE);
            emptyTripsTextView.setVisibility(View.GONE);
        }
    }

    private void addDataToAdapter(Trip trip) {
        mTrips.add(trip);
        mTripsAdapter.notifyDataSetChanged();

        if(mTrips.size() == 0) {
            tripsRecyclerView.setVisibility(View.GONE);
            emptyTripsTextView.setVisibility(View.VISIBLE);
        }
        else {
            tripsRecyclerView.setVisibility(View.VISIBLE);
            emptyTripsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mTrips.clear();
        mTripsReference.addChildEventListener(mTripsEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTripsReference.removeEventListener(mTripsEventListener);
    }
}
