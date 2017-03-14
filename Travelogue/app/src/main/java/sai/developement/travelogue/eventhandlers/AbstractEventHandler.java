package sai.developement.travelogue.eventhandlers;

import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.events.AuthStateChangeEvent;
import sai.developement.travelogue.events.LogoutEvent;
import sai.developement.travelogue.events.SelectAvatarEvent;
import sai.developement.travelogue.events.ShowMessageEvent;
import sai.developement.travelogue.fragments.UserAvatarDialogFragment;
import sai.developement.travelogue.helpers.analytics.FirebaseUserAnalyticsHelper;

/**
 * Created by sai on 3/9/17.
 */

public abstract class AbstractEventHandler implements IEventHandler {

    protected boolean isPaused = true;

    protected final TravelogueActivity mActivity;

    private static final String USER_AVATAR_DIALOG = "user_avatar_dialog";


    public AbstractEventHandler(TravelogueActivity travelogueActivity) {
        this.mActivity = travelogueActivity;
    }

    @Override
    public void onResume() {
        isPaused = false;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        isPaused = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(AuthStateChangeEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(SelectAvatarEvent event) {
        showAvatarDialog(event.mUserId);
    }

    private void showAvatarDialog(String userId) {
        final DialogFragment prevAvatarDialog = (DialogFragment) mActivity.getSupportFragmentManager().findFragmentByTag(USER_AVATAR_DIALOG);

        if(prevAvatarDialog != null) {
            prevAvatarDialog.dismissAllowingStateLoss();
        }

        UserAvatarDialogFragment dialogFragment = UserAvatarDialogFragment.newInstance(userId);
        dialogFragment.show(mActivity.getSupportFragmentManager(), USER_AVATAR_DIALOG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(LogoutEvent event) {
        FirebaseUserAnalyticsHelper.logLogoutEvent();
        CurrentUser.deleteCurrentUser(mActivity);
        AuthUI.getInstance().signOut(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(ShowMessageEvent event) {
        Toast.makeText(mActivity, event.message, Toast.LENGTH_LONG).show();
    }
}
