package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.adapters.TripMatesListAdapter;
import sai.developement.travelogue.eventhandlers.fragments.IFragmentEventHandler;
import sai.developement.travelogue.eventhandlers.fragments.TripDetailsFragmentEventHandler;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.helpers.analytics.FirebaseTravelMateAnalyticsHelper;
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

    @BindView(R.id.user_email_edit_text)
    EditText emailEditText;

    @BindView(R.id.trip_mates_list_view)
    ListView tripmatesListView;

    @BindView(R.id.progress_bar_layout)
    RelativeLayout progressBarLayout;

    @BindView(R.id.users_list_empty_view)
    TextView emptyListView;

    @BindView(R.id.trip_loc_text_view)
    TextView tripLocTextView;

    @BindView(R.id.details_fragment_scroll_view)
    ScrollView mainScrollView;

    @BindView(R.id.add_user_text_layout)
    LinearLayout addUserTextLayout;

    private Trip mTrip;

    private TripMatesListAdapter mTripMatesListAdapter;

    private ArrayList<User> mTripMates = new ArrayList<>();

    private IFragmentEventHandler mEventHandler;

    public TripDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().getParcelable(ViewTripActivity.TRIP_KEY) != null) {
            mTrip = getArguments().getParcelable(ViewTripActivity.TRIP_KEY);
        }

        mEventHandler = new TripDetailsFragmentEventHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
        ButterKnife.bind(this, view);

        addUserButton.setEnabled(false);

        addUserTextLayout.setVisibility(((TravelogueActivity)getActivity()).isConnected() ? View.VISIBLE : View.GONE);

        if(mTrip != null) {
            tripNameTextView.setText(mTrip.getName());
            tripLocTextView.setText(mTrip.getPrimaryLocation());
            startDateTextView.setText(mTrip.getStartDate());
            endDateTextView.setText(mTrip.getEndDate());
            if(mTrip.getTravellers() != null) {
                mTripMates = new ArrayList<>(mTrip.getTravellers());
            }

            if(mTripMates.size() > 0) {
                removeCurrentUser();
            }
            mTripMatesListAdapter = new TripMatesListAdapter(getContext(), mTripMates);
            tripmatesListView.setAdapter(mTripMatesListAdapter);
            tripmatesListView.setEmptyView(emptyListView);
        }
        else {
            // We should really not be here!! :(
        }

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() > 0) {
                    addUserButton.setEnabled(true);
                }
                else{
                    addUserButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!((TravelogueActivity)getActivity()).isConnected()) {
                    Toast.makeText(getContext(), getString(R.string.str_connectivity_lost_message), Toast.LENGTH_LONG).show();
                    return;
                }

                progressBarLayout.setVisibility(View.VISIBLE);
                final String input = emailEditText.getText().toString().trim();
                if(validateEmail(input)) {
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(isAdded()) {
                                progressBarLayout.setVisibility(View.GONE);
                                Logger.d("User found on DB");
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    User user = new User();
                                    Iterator it = ((HashMap) dataSnapshot.getValue()).entrySet().iterator();
                                    while (it.hasNext()) {
                                        Map.Entry pair = (Map.Entry) it.next();
                                        user.setEmail((String) ((HashMap) pair.getValue()).get("email"));
                                        user.setId((String) ((HashMap) pair.getValue()).get("id"));
                                        user.setName((String) ((HashMap) pair.getValue()).get("name"));
                                        user.setAvatarId(((Long)((HashMap) pair.getValue()).get("avatarId")).intValue());
                                        break;
                                    }

                                    verifyAndAddToList(user);
                                } else {
                                    // No user with email found
                                    Logger.d("No user with email " + input + " found.");
                                    Toast.makeText(getActivity(), getString(R.string.str_no_user_found, input), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Logger.e("User Listener cancelled" + databaseError.getMessage());
                        }
                    };
                    FirebaseDatabaseHelper.getUserByEmail(input, valueEventListener);
                }
                else {
                    progressBarLayout.setVisibility(View.GONE);
                    emailEditText.setError(getString(R.string.str_email_error));
                }
            }
        });

        return view;
    }

    private void removeCurrentUser() {
        Iterator<User> tripMatesIterator = mTripMates.iterator();
        while(tripMatesIterator.hasNext()) {
            User user = tripMatesIterator.next();
            if(user.getId().equalsIgnoreCase(CurrentUser.getCurrentuser().getUserId())) {
                tripMatesIterator.remove();
            }
        }
    }

    private void verifyAndAddToList(User newUser) {
        Iterator<User> iterator = mTripMates.iterator();

        boolean alreadyExists = false;
        while(iterator.hasNext()) {
            User currentUser = iterator.next();
            if(newUser.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
                alreadyExists = true;
                break;
            }
        }

        if(!alreadyExists) {
            mTripMates.add(newUser);
            mTripMatesListAdapter.notifyDataSetChanged();
            addUserToTrip(newUser);
        }
        else {
            Toast.makeText(getContext(), getString(R.string.str_user_already_exists, newUser.getEmail()), Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserToTrip(User user) {
        FirebaseDatabaseHelper.addSharedTrip(user.getId(), mTrip);
        FirebaseTravelMateAnalyticsHelper.logTravelMateAddedEvent(mTrip.getId());
    }

    private boolean validateEmail(String email) {
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mainScrollView.requestFocus();
        mEventHandler.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventHandler.onStop();
    }

    public void toggleBehavior(boolean isConnected) {
        addUserTextLayout.setVisibility(isConnected ? View.VISIBLE : View.GONE);
    }
}
