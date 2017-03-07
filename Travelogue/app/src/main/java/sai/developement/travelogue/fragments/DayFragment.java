package sai.developement.travelogue.fragments;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.ItineraryRecyclerAdapter;
import sai.developement.travelogue.adapters.SuggestionsAdapter;
import sai.developement.travelogue.asynctasks.LoadSuggestionsTask;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.helpers.GenerateGUIDHelper;
import sai.developement.travelogue.listeners.RecyclerItemClickListener;
import sai.developement.travelogue.models.Suggestion;
import sai.developement.travelogue.models.TripDay;
import sai.developement.travelogue.models.TripVisit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment {

    private static final String DAY_KEY = "trip_day";
    private static final String DATE_KEY = "trip_date";
    private static final String TRIP_ID_KEY = "trip_id";

    @BindView(R.id.itinerary_recycler_view)
    RecyclerView itineraryRecyclerView;

    @BindView(R.id.empty_itinerary_recycler_text_view)
    TextView emptyItineraryTextView;

    @BindView(R.id.itinerary_progress_bar)
    ProgressBar itineraryProgressBar;

    @BindView(R.id.suggestions_layout)
    FrameLayout suggestionsLayout;

    @BindView(R.id.suggestion_recycler_view)
    RecyclerView suggestionsRecyclerView;

    @BindView(R.id.suggestions_progress_bar)
    ProgressBar suggestionsProgressBar;

    @BindView(R.id.trip_day_text_view)
    TextView tripDayTextView;

    @BindView(R.id.trip_date_text_view)
    TextView tripDateTextView;

    @BindView(R.id.place_edit_text)
    EditText tripPlaceEditText;

    @BindView(R.id.city_edit_text)
    EditText locationEditText;

    @BindView(R.id.from_time_edit_text)
    EditText fromTimeEditText;

    @BindView(R.id.add_itinerary_button)
    TextView addItineraryButton;

    @BindView(R.id.clear_itinerary_button)
    TextView clearItineraryButton;

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

        addItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidated = true;
                if(locationEditText.getText().toString() == null || locationEditText.getText().toString().trim().isEmpty()) {
                    locationEditText.setError("Please enter a valid city.");
                    isValidated = false;
                }

                if(tripPlaceEditText.getText().toString() == null || tripPlaceEditText.getText().toString().trim().isEmpty()) {
                    tripPlaceEditText.setError("Please enter a place to visit.");
                    isValidated = false;
                }


                if(!isValidated) return;

                TripVisit tripVisit = new TripVisit();
                tripVisit.setId(GenerateGUIDHelper.generateGUID(GenerateGUIDHelper.Model.TRIP_VISIT));
                tripVisit.setLocation(locationEditText.getText().toString().trim());
                tripVisit.setPlace(tripPlaceEditText.getText().toString().trim());
                if(fromTimeEditText.getText().toString() != null && !fromTimeEditText.getText().toString().trim().isEmpty()) {
                    tripVisit.setTripTime(fromTimeEditText.getText().toString().trim());
                }

                if(tripDay != null) {
                    FirebaseDatabaseHelper.addTripVisitToDay(tripDay.getId(), tripVisit);
                    clearItiEditTexts();
                }

                else {
                    Toast.makeText(getContext(), "Unable to add trip right now! Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        clearItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItiEditTexts();
            }
        });

        fromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        fromTimeEditText.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("When are you planning to visit?");
                mTimePicker.show();
            }
        });

        tripPlaceEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleItineraryProgress(true);
                showAndLoadSuggestions();
            }
        });

        return view;
    }

    private void showAndLoadSuggestions() {
        LoadSuggestionsTask task = new LoadSuggestionsTask(getContext(), "Raleigh, NC",
                new LoadSuggestionsTask.ISuggestionsCallback() {
                    @Override
                    public void onSuggestionsLoaded(final List<Suggestion> suggestionList) {
                        toggleItineraryProgress(false);
                        SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(suggestionList, getContext());
                        initSuggestionsRecyclerView();
                        suggestionsRecyclerView.setAdapter(suggestionsAdapter);
                        suggestionsLayout.setVisibility(View.VISIBLE);

                        suggestionsRecyclerView.addOnItemTouchListener(
                                new RecyclerItemClickListener(getContext(), suggestionsRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        suggestionsLayout.setVisibility(View.GONE);
                                        tripPlaceEditText.setText(suggestionList.get(position).getName());
                                    }

                                    @Override public void onLongItemClick(View view, int position) {
                                        // do whatever
                                    }
                                })
                        );
                    }

                    private void initSuggestionsRecyclerView() {
                        suggestionsRecyclerView.setHasFixedSize(true);

                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                        suggestionsRecyclerView.setLayoutManager(layoutManager);
                    }
                });
        task.execute();
    }

    private void clearItiEditTexts() {
        locationEditText.setText(null);
        tripPlaceEditText.setText(null);
        fromTimeEditText.setText(null);
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
        loadItinerary();
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

                    initChildEventListener();
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

}
