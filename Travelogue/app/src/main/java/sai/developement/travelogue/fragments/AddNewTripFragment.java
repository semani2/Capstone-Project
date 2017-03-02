package sai.developement.travelogue.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;

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

    private Calendar startCalendar = null;
    private Calendar endCalendar;

    public AddNewTripFragment() {
        // Required empty public constructor
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

        return view;
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
            }

            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            textInputEditText.setText(sdf.format(calendar.getTime()));
        }
    }

}
