package sai.developement.travelogue.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.AvatarAdapter;
import sai.developement.travelogue.events.ShowMessageEvent;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;

/**
 * Created by sai on 3/8/17.
 */

public class UserAvatarDialogFragment extends DialogFragment {

    private static final String USER_ID_KEY = "user_id";

    private String mUserId = null;

    @BindView(R.id.avatar_grid_view)
    GridView avatarGridView;

    @BindView(R.id.save_avatar_layout)
    LinearLayout saveAvatarLayout;

    @BindView(R.id.save_avatar_button)
    Button saveAvatarButton;

    @BindView(R.id.dialog_close_button)
    ImageView dialogCloseButton;

    @BindView(R.id.cancel_avatar_button)
    Button cancelAvatarButton;

    @BindView(R.id.progress_bar_layout)
    LinearLayout progressBarLayout;

    private AvatarAdapter mAvatarAdapter;

    public static UserAvatarDialogFragment newInstance(String userId) {
        UserAvatarDialogFragment userAvatarDialogFragment = new UserAvatarDialogFragment();

        Bundle args = new Bundle();
        args.putString(USER_ID_KEY, userId);
        userAvatarDialogFragment.setArguments(args);

        userAvatarDialogFragment.setCancelable(false);

        return userAvatarDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
        mUserId = getArguments().getString(USER_ID_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.user_avatar_select_dialog, container, false);
        ButterKnife.bind(this, v);

        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        mAvatarAdapter = new AvatarAdapter(getContext());
        avatarGridView.setAdapter(mAvatarAdapter);

        avatarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final CardView cardView = (CardView)view;
                cardView.setCardBackgroundColor(getResources().getColor(R.color.avatar_bg));

                saveAvatarLayout.setVisibility(View.VISIBLE);
                avatarGridView.setEnabled(false);

                saveAvatarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        progressBarLayout.setVisibility(View.VISIBLE);
                        saveUserAvatar(position);
                        saveAvatarLayout.setEnabled(false);
                    }
                });

                cancelAvatarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        avatarGridView.setEnabled(true);
                        saveAvatarLayout.setVisibility(View.GONE);
                    }
                });
            }
        });


        return v;
    }

    private void saveUserAvatar(int position) {
        FirebaseDatabaseHelper.saveUserAvatar(mUserId, position + 1, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressBarLayout.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    EventBus.getDefault().post(new ShowMessageEvent(getString(R.string.str_avatar_saved)));
                    dismiss();
                }
                else {
                    EventBus.getDefault().post(new ShowMessageEvent(getString(R.string.str_avatar_save_failed)));
                    avatarGridView.setEnabled(true);
                    saveAvatarLayout.setEnabled(true);
                    saveAvatarLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
