package sai.developement.travelogue.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.adapters.TripMatesListAdapter;
import sai.developement.travelogue.asynctasks.LoadPlaceImageTask;
import sai.developement.travelogue.eventhandlers.fragments.AddNewTripFragmentEventHandler;
import sai.developement.travelogue.eventhandlers.fragments.IFragmentEventHandler;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.helpers.GenerateGUIDHelper;
import sai.developement.travelogue.helpers.analytics.FirebaseTripAnalyticsHelper;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripDay;
import sai.developement.travelogue.models.User;
import sai.developement.travelogue.widget.TravelogueWidgetProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTripFragment extends Fragment {

    @BindView(R.id.trip_name_text_input_layout)
    TextInputLayout tripNameTextInputLayout;

    @BindView(R.id.trip_name_text_edit_text)
    TextInputEditText tripNameEditText;

    @BindView(R.id.start_date_edit_text)
    EditText startDateEditText;

    @BindView(R.id.end_date_edit_text)
    EditText endDateEditText;

    @BindView(R.id.add_users_layout)
    LinearLayout addUsersLayout;

    @BindView(R.id.trip_mates_list_view)
    ListView tripMatesListView;

    @BindView(R.id.users_list_empty_view)
    TextView usersListEmptyView;

    @BindView(R.id.user_emaiL_edit_text)
    EditText addUserEmailEditText;

    @BindView(R.id.add_user_button)
    Button addUserButton;

    @BindView(R.id.save_trip_button)
    Button saveTripButton;

    @BindView(R.id.progress_bar_layout)
    RelativeLayout progressBarLayout;

    private PlaceAutocompleteFragment mPlaceAutocompleteFragment;

    private Calendar startCalendar = null;

    private Calendar endCalendar = null;

    private TripMatesListAdapter listAdapter;

    private ArrayList<User> travelMatesList = new ArrayList<>();

    private Place mTripPlace = null;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    private IFragmentEventHandler mEventHandler;

    private static final String TRIP_NAME_KEY = "trip_name";
    private static final String TRIP_START_DATE_KEY = "trip_start_date";
    private static final String TRIP_END_DATE_KEY = "trip_end_date";
    private static final String TRAVELLERS_KEY = "travellers";

    public AddNewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventHandler.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventHandler.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventHandler = new AddNewTripFragmentEventHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_trip, container, false);
        ButterKnife.bind(this, view);

        mPlaceAutocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        mPlaceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Logger.d("Place for trip selected " + place.getName());
                tripPlaceAdded(place);
            }

            @Override
            public void onError(Status status) {
                Logger.e(status.getStatusMessage());
            }
        });

        final View.OnClickListener startDateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendar = Calendar.getInstance();
                startCalendar.setTimeInMillis(System.currentTimeMillis() - 1000);
                showDatePicker(startDateEditText, startCalendar, true);
            }
        };
        startDateEditText.setOnClickListener(startDateClickListener);

        final View.OnClickListener endDateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(endDateEditText, startCalendar, false);
            }
        };
        endDateEditText.setOnClickListener(endDateClickListener);

        listAdapter = new TripMatesListAdapter(getContext(), travelMatesList);
        tripMatesListView.setAdapter(listAdapter);
        tripMatesListView.setEmptyView(usersListEmptyView);

        if(savedInstanceState != null) {
            if(savedInstanceState.getString(TRIP_NAME_KEY) != null) {
                tripNameEditText.setText(savedInstanceState.getString(TRIP_NAME_KEY));
            }
            if(savedInstanceState.getSerializable(TRIP_START_DATE_KEY) != null)  {
                startCalendar = (Calendar)savedInstanceState.getSerializable(TRIP_START_DATE_KEY);
                startDateEditText.setText(dateFormatForMonth.format(startCalendar.getTime()));
            }
            if(savedInstanceState.getSerializable(TRIP_END_DATE_KEY) != null) {
                endCalendar = (Calendar)savedInstanceState.getSerializable(TRIP_END_DATE_KEY);
                endDateEditText.setText(dateFormatForMonth.format(endCalendar.getTime()));
            }
            if(savedInstanceState.getParcelableArray(TRAVELLERS_KEY) != null && savedInstanceState.getParcelableArray(TRAVELLERS_KEY).length > 0) {
                travelMatesList.clear();
                User[] travelMates = (User[])savedInstanceState.getParcelableArray(TRAVELLERS_KEY);
                travelMatesList.addAll(Arrays.asList(travelMates));
                listAdapter.notifyDataSetChanged();
            }
        }

        if(startCalendar == null) {
            endDateEditText.setEnabled(false);
        }

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        saveTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrip();
            }
        });

        boolean isConnected = ((TravelogueActivity) getActivity()).isConnected();
        addUserButton.setEnabled(isConnected);
        saveTripButton.setEnabled(isConnected);

        return view;
    }

    private void tripPlaceAdded(Place place) {
        // Check if Place is a country, locality, if not show a message to the user
        List<Integer> placeTypes = place.getPlaceTypes();
        Iterator placeTypeIterator = placeTypes.iterator();
        boolean isCityOrCountry = false;
        while(placeTypeIterator.hasNext()) {
            switch ((Integer)placeTypeIterator.next()) {
                case Place.TYPE_COUNTRY:
                    isCityOrCountry = true;
                    break;

                case Place.TYPE_LOCALITY:
                    isCityOrCountry = true;
                    break;

                default:
                    isCityOrCountry = false;
                    break;
            }
            if(isCityOrCountry) {
                break;
            }
        }
        if(!isCityOrCountry) {
            Toast.makeText(getContext(), getString(R.string.str_select_valid_city), Toast.LENGTH_LONG).show();
        }
        else {
            mTripPlace = place;
        }
    }

    private void saveTrip() {
        if(validateInput(tripNameEditText, startDateEditText, endDateEditText))
        {
            toggleProgressBar(true);
            String tripId = GenerateGUIDHelper.generateGUID(GenerateGUIDHelper.Model.TRIP);

            String startDate = dateFormatForMonth.format(startCalendar.getTime());
            String endDate = dateFormatForMonth.format(endCalendar.getTime());
            int duration = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()));

            String tripName = tripNameEditText.getText().toString().trim();

            final Trip trip = new Trip();
            trip.setId(tripId);
            trip.setName(tripName);
            trip.setStartDate(startDate);
            trip.setEndDate(endDate);
            trip.setDuration(duration + 1);
            trip.setTravellers(travelMatesList);
            trip.setStartDateMillis(startCalendar.getTimeInMillis());
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                trip.setCreatedByUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                trip.setCreateByUseremail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }

            LoadPlaceImageTask loadPlaceImageTask = new LoadPlaceImageTask(trip, mTripPlace, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()) {
                        // Create the trip day objects with the required dates
                        addTripDays(trip);
                        Logger.d("New trip created successfully : " +trip.getId());
                        toggleProgressBar(false);
                        updateWidget();
                        logAnalyticsEvent(trip.getId());
                        Toast.makeText(getContext(), getString(R.string.str_new_trip_created, trip.getName()), Toast.LENGTH_LONG).show();
                        goToViewActivity(trip);
                    }
                    else {
                        toggleProgressBar(false);
                        Logger.e("Error creating new trip", task.getException());
                    }
                }
            });

            loadPlaceImageTask.execute();
        }
        else {
            // Set error tet messages
        }
    }

    private void logAnalyticsEvent(String tripId) {
        FirebaseTripAnalyticsHelper.logTripAddedEvent(tripId);
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

    private void addTripDays(Trip trip) {
        for(int i = 0; i<trip.getDuration();i++){
            Calendar date = startCalendar;
            String tripDayId = GenerateGUIDHelper.generateGUID(GenerateGUIDHelper.Model.TRIP_DAY);
            TripDay tripDay = new TripDay();
            tripDay.setId(tripDayId);
            date.add(Calendar.DATE, i);
            tripDay.setDate(dateFormatForMonth.format(date.getTime()));

            FirebaseDatabaseHelper.addTripDayForTrip(trip, tripDay);
        }
    }

    private void goToViewActivity(Trip trip) {
        Intent viewIntent = new Intent(getActivity(), ViewTripActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(ViewTripActivity.TRIP_KEY, trip);
        viewIntent.putExtras(extras);
        startActivity(viewIntent);
        getActivity().finish();
    }

    private boolean validateInput(EditText... editTexts) {
        boolean validInput = true;

        for(int i=0; i< editTexts.length;i++) {
            //Clearing out old errors
            editTexts[i].setError(null);

            if(editTexts[i].getText().toString() == null || editTexts[i].getText().toString().trim().isEmpty()) {
                editTexts[i].setError(getString(R.string.str_required_field));
                validInput = false;
            }
        }

        if(mTripPlace == null) {
            validInput = false;
        }

        return validInput;
    }

    private void addUser() {
        toggleProgressBar(true);
        final String email = addUserEmailEditText.getText().toString().trim();
        if(validateEmail(email)) {
            //Add user
            addUserEmailEditText.setError(null);
            final ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(isAdded()) {
                        toggleProgressBar(false);
                        Logger.d("User found on DB");
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            User user = new User();
                            Iterator it = ((HashMap) dataSnapshot.getValue()).entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                user.setEmail((String) ((HashMap) pair.getValue()).get("email"));
                                user.setId((String) ((HashMap) pair.getValue()).get("id"));
                                user.setName((String) ((HashMap) pair.getValue()).get("name"));
                                user.setAvatarId(((Long)  ((HashMap) pair.getValue()).get("avatarId")).intValue());
                                break;
                            }

                            verifyAndAddToList(user);
                        } else {
                            // No user with email found
                            Logger.d("No user with email " + email + " found.");
                            Toast.makeText(getActivity(), getString(R.string.str_no_user_found, email), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Logger.e("User Listener cancelled" + databaseError.getMessage());
                }
            };
            FirebaseDatabaseHelper.getUserByEmail(email, valueEventListener);
            addUserEmailEditText.setText(null);
        }
        else {
            toggleProgressBar(false);
            addUserEmailEditText.setError(getString(R.string.str_email_error));
        }
    }

    private void verifyAndAddToList(User newUser) {
        Iterator<User> iterator = travelMatesList.iterator();

        boolean alreadyExists = false;
        while(iterator.hasNext()) {
            User currentUser = iterator.next();
            if(newUser.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
                alreadyExists = true;
                break;
            }
        }
        if(!alreadyExists) {
            travelMatesList.add(newUser);
            listAdapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(getContext(), getString(R.string.str_user_already_exists, newUser.getEmail()), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail(String email) {
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void showDatePicker(EditText editText, Calendar displayCalendar,boolean isStart) {
        MyDatePickerCallback datePickerCallback = new MyDatePickerCallback(editText, isStart);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), datePickerCallback, displayCalendar.get(Calendar.YEAR),
                displayCalendar.get(Calendar.MONTH), displayCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(displayCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(tripNameEditText.getText()!= null && !tripNameEditText.getText().toString().trim().isEmpty()) {
            outState.putString(TRIP_NAME_KEY, tripNameEditText.getText().toString());
        }

        if(startCalendar != null) {
            outState.putSerializable(TRIP_START_DATE_KEY, startCalendar);
        }

        if(endCalendar != null) {
            outState.putSerializable(TRIP_END_DATE_KEY, endCalendar);
        }

        if(travelMatesList.size() > 0) {
            User[] userArray = new User[travelMatesList.size()];
            outState.putParcelableArray(TRAVELLERS_KEY, travelMatesList.toArray(userArray));
        }
    }

    class MyDatePickerCallback implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

        private final EditText editText;
        private final boolean isStart;

        public MyDatePickerCallback(EditText textInputEditText, boolean isStart) {
            this.editText = textInputEditText;
            this.isStart = isStart;
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            dialogInterface.dismiss();
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if(isStart) {
                startCalendar = calendar;
                endDateEditText.setEnabled(true);
            }
            else {
                endCalendar = calendar;
            }

            editText.setText(dateFormatForMonth.format(calendar.getTime()));
            editText.clearFocus();
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void toggleProgressBar(boolean isBusy) {
        progressBarLayout.setVisibility(isBusy ? View.VISIBLE : View.GONE);
    }

    public void toggleBehavior(boolean isConnected) {
        addUserButton.setEnabled(isConnected);
        saveTripButton.setEnabled(isConnected);
    }

}
