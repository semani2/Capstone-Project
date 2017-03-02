package sai.developement.travelogue.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.TripMatesListAdapter;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTripFragment extends Fragment {

    @BindView(R.id.trip_name_text_input_layout)
    TextInputLayout tripNameTextInputLayout;

    @BindView(R.id.trip_name_text_edit_text)
    TextInputEditText tripNameEditText;

    @BindView(R.id.start_date_edit_text)
    TextInputEditText startDateEditText;

    @BindView(R.id.end_date_edit_text)
    TextInputEditText endDateEditText;

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

    private Calendar startCalendar = null;

    private Calendar endCalendar;

    private TripMatesListAdapter listAdapter;

    private final ArrayList<User> travelMatesList = new ArrayList<>();

    private DatabaseReference mUsersReference;

    private ValueEventListener mValueEventListener;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    public AddNewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsersReference = FirebaseDatabase.getInstance().getReference().child(FirebaseDatabaseHelper.DB_NODE_USERS);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_trip, container, false);
        ButterKnife.bind(this, view);

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

        if(startCalendar == null) {
            endDateEditText.setEnabled(false);
        }

        listAdapter = new TripMatesListAdapter(getContext(), travelMatesList);
        tripMatesListView.setAdapter(listAdapter);
        tripMatesListView.setEmptyView(usersListEmptyView);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        return view;
    }

    private void addUser() {
        String email = addUserEmailEditText.getText().toString().trim();
        if(validateEmail(email)) {
            //Add user
            addUserEmailEditText.setError(null);
            mUsersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Logger.d("User found on DB");
                    if(dataSnapshot != null) {
                        User user = new User();
                        Iterator it = ((HashMap)dataSnapshot.getValue()).entrySet().iterator();
                        while(it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            user.setEmail((String)((HashMap)pair.getValue()).get("email"));
                            user.setId((String)((HashMap)pair.getValue()).get("id"));
                            user.setName((String)((HashMap)pair.getValue()).get("name"));
                            break;
                        }
                        travelMatesList.add(user);
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    com.orhanobut.logger.Logger.e("User Listener cancelled"+databaseError.getMessage());
                }
            });
            addUserEmailEditText.setText(null);
        }
        else {
            addUserEmailEditText.setError(getString(R.string.str_email_error));
        }
    }

    private boolean validateEmail(String email) {
        if (email == null || TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void showDatePicker(TextInputEditText editText, Calendar displayCalendar,boolean isStart) {
        MyDatePickerCallback datePickerCallback = new MyDatePickerCallback(editText, isStart);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), datePickerCallback, displayCalendar.get(Calendar.YEAR),
                displayCalendar.get(Calendar.MONTH), displayCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(displayCalendar.getTimeInMillis());
        datePickerDialog.show();
    }



    class MyDatePickerCallback implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

        private final TextInputEditText textInputEditText;
        private final boolean isStart;

        public MyDatePickerCallback(TextInputEditText textInputEditText, boolean isStart) {
            this.textInputEditText = textInputEditText;
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
                showTripMatesLayout();
            }

            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            textInputEditText.setText(sdf.format(calendar.getTime()));
            textInputEditText.clearFocus();
            hideKeyboard();
        }
    }

    private void showTripMatesLayout() {

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

}
