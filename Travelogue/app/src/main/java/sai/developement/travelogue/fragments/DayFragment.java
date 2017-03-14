package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.ItineraryRecyclerAdapter;
import sai.developement.travelogue.callbacks.ItineraryCallback;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.helpers.analytics.FirebaseItineraryAnalyticsHelper;
import sai.developement.travelogue.models.TripDay;
import sai.developement.travelogue.models.TripVisit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment implements ItineraryCallback{

    private static final String DAY_KEY = "trip_day";
    private static final String DATE_KEY = "trip_date";
    private static final String TRIP_ID_KEY = "trip_id";

    private static final String ADD_ITI_FRAGMENT = "add_iti_fragment";

    @BindView(R.id.itinerary_recycler_view)
    RecyclerView itineraryRecyclerView;

    @BindView(R.id.empty_itinerary_recycler_text_view)
    TextView emptyItineraryTextView;

    @BindView(R.id.itinerary_progress_bar)
    ProgressBar itineraryProgressBar;

    @BindView(R.id.trip_day_text_view)
    TextView tripDayTextView;

    @BindView(R.id.trip_date_text_view)
    TextView tripDateTextView;

    @BindView(R.id.itinerary_add_fab)
    FloatingActionButton itineraryAddFab;

    private int mTripDay;

    private String mTripDate = null;

    private String mTripId = null;

    private TripDay tripDay = null;

    private DatabaseReference mTripDayDatabaseReference;

    private ChildEventListener mTripVisitsChildEventListener;

    private List<TripVisit> mTripVisitList = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;

    private ItineraryRecyclerAdapter mItineraryRecyclerAdapter;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(int day, String date, String tripId) {
        DayFragment dayFragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_KEY, day);
        args.putString(DATE_KEY, date);
        args.putString(TRIP_ID_KEY, tripId);
        dayFragment.setArguments(args);
        return dayFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTripDay = getArguments().getInt(DAY_KEY);
        mTripDate = getArguments().getString(DATE_KEY);
        mTripId = getArguments().getString(TRIP_ID_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        ButterKnife.bind(this, view);

        tripDayTextView.setText(getString(R.string.str_trip_day, mTripDay));
        tripDateTextView.setText(mTripDate);

        initItiRecyclerView();

        itineraryAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItineraryDialogFragment addItineraryDialogFragment = AddItineraryDialogFragment.newInstance();
                addItineraryDialogFragment.setTargetFragment(DayFragment.this, 0);

                addItineraryDialogFragment.show(getActivity().getSupportFragmentManager(),
                        ADD_ITI_FRAGMENT);
            }
        });

        initChildEventListener();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mTripDayDatabaseReference != null) {
            mTripDayDatabaseReference.removeEventListener(mTripVisitsChildEventListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mTripVisitList.clear();
        mItineraryRecyclerAdapter.notifyDataSetChanged();
        loadItinerary();
        if(mTripDayDatabaseReference != null) {
            mTripDayDatabaseReference.addChildEventListener(mTripVisitsChildEventListener);
        }
    }

    private void initItiRecyclerView() {
        itineraryRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        itineraryRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mItineraryRecyclerAdapter = new ItineraryRecyclerAdapter(getActivity(), mTripVisitList);
        itineraryRecyclerView.setAdapter(mItineraryRecyclerAdapter);

        if(mTripVisitList.size() == 0) {
            itineraryRecyclerView.setVisibility(View.GONE);
            emptyItineraryTextView.setVisibility(View.VISIBLE);
        }
        else {
            itineraryRecyclerView.setVisibility(View.VISIBLE);
            emptyItineraryTextView.setVisibility(View.GONE);
        }
    }

    private void loadItinerary() {
        if(tripDay != null) {
            mTripDayDatabaseReference.addChildEventListener(mTripVisitsChildEventListener);
            return;
        }

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    // Parse into trip day here
                    // Create child event listener into its children
                    TripDay tripDay1 = new TripDay();
                    Iterator it = ((HashMap) dataSnapshot.getValue()).entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        tripDay1.setId((String) ((HashMap) pair.getValue()).get("id"));
                        tripDay1.setDate((String) ((HashMap) pair.getValue()).get("date"));
                        break;
                    }

                    tripDay = tripDay1;
                    mTripDayDatabaseReference = FirebaseDatabaseHelper.getTripVisitsReference()
                            .child(tripDay.getId());

                    mTripDayDatabaseReference.addChildEventListener(mTripVisitsChildEventListener);

                }
                else {
                    Logger.e("Trip day not created. Creating one now...");
                    // TODO :: Create a trip day here
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e("Fetching Trip Day cancelled " + databaseError.getMessage());
            }
        };

        FirebaseDatabaseHelper.getTripDay(mTripId, mTripDate, valueEventListener);
    }

    private void initChildEventListener() {
        mTripVisitsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    addTripVisitToAdapter(dataSnapshot.getValue(TripVisit.class));
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

    private void addTripVisitToAdapter(TripVisit tripVisit) {
        Iterator<TripVisit> tripVisitIterator = mTripVisitList.iterator();
        while(tripVisitIterator.hasNext()) {
            if(tripVisitIterator.next().getId().equalsIgnoreCase(tripVisit.getId())) {
                tripVisitIterator.remove();
            }
        }
        mTripVisitList.add(tripVisit);

        mItineraryRecyclerAdapter.notifyDataSetChanged();

        if(mTripVisitList.size() == 0) {
            itineraryRecyclerView.setVisibility(View.GONE);
            emptyItineraryTextView.setVisibility(View.VISIBLE);
        }
        else {
            itineraryRecyclerView.setVisibility(View.VISIBLE);
            emptyItineraryTextView.setVisibility(View.GONE);
        }
    }

    private void toggleItineraryProgress(boolean isBusy) {
        itineraryProgressBar.setVisibility(isBusy ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItinerarySelected(TripVisit tripVisit) {
        FirebaseDatabaseHelper.addTripVisitToDay(tripDay.getId(), tripVisit);
        FirebaseItineraryAnalyticsHelper.logItineraryAddedEvent(tripDay.getId());
    }
}
