package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.events.ShowMessageEvent;

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

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        sendButton.setEnabled(false);

        chatMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
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
                EventBus.getDefault().post(new ShowMessageEvent("Sending Message!! " + chatMessageEditText.getText().toString()));
                chatMessageEditText.setText(null);
            }
        });

        return view;
    }

}
