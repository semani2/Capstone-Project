package sai.developement.travelogue.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
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
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.adapters.TripsRecyclerAdapter;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.listeners.RecyclerItemClickListener;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.widget.TravelogueWidgetProvider;

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
        return view;
    }


    private void initEventListener() {
        mTripsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    addDataToAdapter(dataSnapshot.getValue(Trip.class));
                }
                updateWidget();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    removeTrip(dataSnapshot.getValue(Trip.class));
                }
                updateWidget();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void removeTrip(Trip value) {
        Iterator<Trip> currentTrips = mTrips.iterator();

        while(currentTrips.hasNext()) {
            if(currentTrips.next().getId().equalsIgnoreCase(value.getId())) {
                currentTrips.remove();
            }
        }
        mTripsAdapter.notifyDataSetChanged();
    }

    private void updateWidget() {
        ComponentName name = new ComponentName(getActivity(), TravelogueWidgetProvider.class);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(getActivity());
        int[] ids = widgetManager.getAppWidgetIds(name);
        Intent intent = new Intent(getActivity(), TravelogueWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(AppWidgetManager.ACTION_APPWIDGET_UPDATE, ids);

        widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);

        getActivity().sendBroadcast(intent);
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

        tripsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), tripsRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Bundle extras = new Bundle();
                        extras.putParcelable(ViewTripActivity.TRIP_KEY, mTrips.get(position));

                        Intent intent = new Intent(getActivity(), ViewTripActivity.class);
                        intent.putExtras(extras);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                new Pair<View, String>(view.findViewById(R.id.trip_name_text_view),
                                        getString(R.string.str_transition_trip_name)),
                                new Pair<View, String>(view.findViewById(R.id.trip_loc_text_view),
                                        getString(R.string.str_transition_trip_loc)),
                                new Pair<View, String>(view.findViewById(R.id.trip_image_view),
                                        getString(R.string.str_transition_trip_image))
                        );
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        if(mTripsFlag == HomeActivity.MY_TRIPS_FLAG) {
                            showDeleteAlert(position);
                        }
                    }
                })
        );
    }

    private void showDeleteAlert(final int position) {
        Trip mTrip = mTrips.get(position);
        new AlertDialog.Builder(getContext())
                .setTitle(mTrip.getName())
                .setMessage(getString(R.string.str_delete_trip, mTrip.getName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabaseHelper.deleteTrip(mTrips.get(position));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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
        if(mTripsFlag == HomeActivity.MY_TRIPS_FLAG) {
            mTripsReference = FirebaseDatabaseHelper.getTripsDatabaseReference().
                    child(mUserId);
        }
        else {
            mTripsReference = FirebaseDatabaseHelper.getSharedTripsReference().
                    child(mUserId);
        }

        initEventListener();
        mTrips.clear();
        mTripsReference.addChildEventListener(mTripsEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTrips.clear();
        mTripsAdapter.notifyDataSetChanged();
        mTripsReference.removeEventListener(mTripsEventListener);
    }
}
