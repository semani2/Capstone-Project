package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.ChatActivity;
import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.adapters.ChatAdapter;
import sai.developement.travelogue.eventhandlers.fragments.ChatFragmentEventHandler;
import sai.developement.travelogue.eventhandlers.fragments.IFragmentEventHandler;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.helpers.analytics.FirebaseChatAnalyticsHelper;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.chat_message_edit_text)
    EditText chatMessageEditText;

    @BindView(R.id.chat_message_send_button)
    ImageButton sendButton;

    @BindView(R.id.progress_bar_layout)
    LinearLayout progressBarLayout;

    private LinearLayoutManager mLayoutManager;

    private ChatAdapter mChatAdapter;

    private List<TripMessage> mTripMessages = new ArrayList<>();

    private DatabaseReference mTripChatReference;

    private ChildEventListener mChatChildEventListener;

    private Trip mTrip;

    private CurrentUser mCurrentUser;

    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("dd MMM, HH:mm", Locale.US);

    private IFragmentEventHandler mEventHandler;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventHandler = new ChatFragmentEventHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        mTrip = getArguments().getParcelable(ChatActivity.TRIP_KEY);

        mCurrentUser = CurrentUser.getCurrentuser();

        sendButton.setEnabled(false);

        chatMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(((TravelogueActivity) getActivity()).isConnected());
                }
                else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!((TravelogueActivity)getActivity()).isConnected()) {
                    Toast.makeText(getContext(), getString(R.string.str_connectivity_lost_message), Toast.LENGTH_LONG).show();
                    return;
                }
                sendMessage();
                chatMessageEditText.setText(null);
            }
        });

        initRecyclerView();

        mTripChatReference = FirebaseDatabaseHelper.getChatDatabaseReference(mTrip.getId());

        mChatChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TripMessage tripMessage = dataSnapshot.getValue(TripMessage.class);
                mTripMessages.add(tripMessage);
                mChatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(mTripMessages.size() - 1);
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

        FirebaseChatAnalyticsHelper.logChatOpenedEvent(mTrip.getId());
        return view;
    }

    private void sendMessage() {
        TripMessage tripMessage = new TripMessage();
        tripMessage.setUserName(mCurrentUser.getUserName());
        tripMessage.setUserAvatarId(mCurrentUser.getUserAvatarId());
        tripMessage.setSentTime(mTimeFormat.format(Calendar.getInstance().getTime()));
        tripMessage.setUserId(mCurrentUser.getUserId());
        tripMessage.setMessage(chatMessageEditText.getText().toString().trim());

        mTripChatReference.push().setValue(tripMessage);
        FirebaseChatAnalyticsHelper.logChatsgSentEvent(mTrip.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventHandler.onStart();
        mTripMessages.clear();
        mTripChatReference.addChildEventListener(mChatChildEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventHandler.onStop();
        mTripChatReference.removeEventListener(mChatChildEventListener);
    }

    private void initRecyclerView() {
        chatRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mChatAdapter = new ChatAdapter(getContext(), mTripMessages);
        chatRecyclerView.setAdapter(mChatAdapter);

    }

    public void toggleBehavior(boolean isConnected) {
        sendButton.setEnabled(isConnected);
    }

}
