package sai.developement.travelogue.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.SuggestionsAdapter;
import sai.developement.travelogue.asynctasks.LoadSuggestionTaskLoader;
import sai.developement.travelogue.callbacks.ItineraryCallback;
import sai.developement.travelogue.helpers.GenerateGUIDHelper;
import sai.developement.travelogue.listeners.RecyclerItemClickListener;
import sai.developement.travelogue.models.Suggestion;
import sai.developement.travelogue.models.TripVisit;

/**
 * Created by sai on 3/10/17.
 */

public class AddItineraryDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<Suggestion>> {

    @BindView(R.id.suggestions_layout)
    FrameLayout suggestionsLayout;

    @BindView(R.id.suggestion_recycler_view)
    RecyclerView suggestionsRecyclerView;

    @BindView(R.id.suggestions_progress_bar)
    ProgressBar suggestionsProgressBar;

    @BindView(R.id.place_edit_text)
    EditText tripPlaceEditText;

    @BindView(R.id.from_time_edit_text)
    EditText fromTimeEditText;

    @BindView(R.id.add_itinerary_button)
    TextView addItineraryButton;

    @BindView(R.id.clear_itinerary_button)
    TextView clearItineraryButton;

    @BindView(R.id.itinerary_dialog_close)
    ImageView closeDialogView;

    @BindView(R.id.location_edit_text)
    EditText locationEditText;

    private ItineraryCallback mCallback = null;

    private Suggestion mselectedSuggestion = new Suggestion();

    private static final int SUGGESTIONS_LOADER = 1;

    private String mLocation = null;

    private ArrayList<Suggestion> mSuggestionsList = new ArrayList<>();

    private SuggestionsAdapter mSuggestionsAdapter;

    private static final String SUGGESTION_LIST_KEY = "suggestion_list_key";

    public static AddItineraryDialogFragment newInstance() {
        return new AddItineraryDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.itinerary_planner_dialog, container, false);
        ButterKnife.bind(this, v);

        tripPlaceEditText.setEnabled(false);

        addItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidated = true;
                if(tripPlaceEditText.getText().toString() == null || tripPlaceEditText.getText().toString().trim().isEmpty()) {
                    tripPlaceEditText.setError("Please enter a place to visit.");
                    isValidated = false;
                }

                if(!isValidated) return;

                TripVisit tripVisit = new TripVisit();
                tripVisit.setId(GenerateGUIDHelper.generateGUID(GenerateGUIDHelper.Model.TRIP_VISIT));
                tripVisit.setLocation(mLocation);
                tripVisit.setPlace(tripPlaceEditText.getText().toString().trim());
                tripVisit.setImageUrl(mselectedSuggestion.getPhotoUrl());
                if(fromTimeEditText.getText().toString() != null && !fromTimeEditText.getText().toString().trim().isEmpty()) {
                    tripVisit.setTripTime(fromTimeEditText.getText().toString().trim());
                }

                if(mCallback != null) {
                    Logger.d("Passing trip visit back to Day Fragment");
                    mCallback.onItinerarySelected(tripVisit);
                    dismissAllowingStateLoss();
                }

            }
        });

        clearItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItiEditor();
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
                getActivity().getSupportLoaderManager().destroyLoader(SUGGESTIONS_LOADER);
                getActivity().getSupportLoaderManager().initLoader(SUGGESTIONS_LOADER, null, AddItineraryDialogFragment.this).forceLoad();
            }
        });

        closeDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setLocation(editable.toString());
            }
        });

        initSuggestionsRecyclerView();

        if(savedInstanceState != null && savedInstanceState.getParcelableArrayList(SUGGESTION_LIST_KEY) != null) {
            ArrayList<Suggestion> suggestions = savedInstanceState.getParcelableArrayList(SUGGESTION_LIST_KEY);
            mSuggestionsList.clear();
            mSuggestionsList.addAll(suggestions);
            suggestionsLayout.setVisibility(View.VISIBLE);
            mSuggestionsAdapter.notifyDataSetChanged();
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ItineraryCallback) this.getTargetFragment();
    }

    private void setLocation(String location) {
        clearSuggestions();
        mLocation = location;
        tripPlaceEditText.setEnabled(mLocation != null);
        fromTimeEditText.setEnabled(mLocation != null);
    }

    private void clearSuggestions() {
        suggestionsLayout.setVisibility(View.GONE);
        mSuggestionsList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
    }

    private void clearItiEditor() {
        locationEditText.setText(null);
        tripPlaceEditText.setText(null);
        fromTimeEditText.setText(null);
    }

    private void initSuggestionsRecyclerView() {
        suggestionsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        suggestionsRecyclerView.setLayoutManager(layoutManager);

        mSuggestionsAdapter = new SuggestionsAdapter(mSuggestionsList, getContext());

        suggestionsRecyclerView.setAdapter(mSuggestionsAdapter);
    }

    private void addSuggestionToPlanner(Suggestion suggestion) {
        mselectedSuggestion = suggestion;
        tripPlaceEditText.setText(suggestion.getName());
    }

    private void showAndLoadSuggestions(final List<Suggestion> suggestionList) {
        suggestionsLayout.setVisibility(View.VISIBLE);
        mSuggestionsList.clear();
        mSuggestionsList.addAll(suggestionList);

        mSuggestionsAdapter.notifyDataSetChanged();

        suggestionsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), suggestionsRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        addSuggestionToPlanner(mSuggestionsList.get(position));
                        clearSuggestions();

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mSuggestionsList.size() > 0) {
            outState.putParcelableArrayList(SUGGESTION_LIST_KEY, mSuggestionsList);
        }
    }

    @Override
    public Loader<List<Suggestion>> onCreateLoader(int id, Bundle args) {
        return new LoadSuggestionTaskLoader(getContext(), mLocation);
    }

    @Override
    public void onLoadFinished(Loader<List<Suggestion>> loader, List<Suggestion> data) {
        if(data == null || data.size() == 0) {
            Toast.makeText(getContext(), getContext().getString(R.string.str_error_fetching_suggestions, mLocation), Toast.LENGTH_LONG).show();
            suggestionsLayout.setVisibility(View.GONE);
            return;
        }
        showAndLoadSuggestions(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Suggestion>> loader) {
        showAndLoadSuggestions(new ArrayList<Suggestion>());
    }

}
