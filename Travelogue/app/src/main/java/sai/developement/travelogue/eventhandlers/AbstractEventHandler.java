package sai.developement.travelogue.eventhandlers;

import com.firebase.ui.auth.AuthUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.events.AuthStateChangeEvent;
import sai.developement.travelogue.events.LogoutEvent;
import sai.developement.travelogue.events.SelectAvatarEvent;

/**
 * Created by sai on 3/9/17.
 */

public abstract class AbstractEventHandler implements IEventHandler {

    protected boolean isPaused = true;

    protected final TravelogueActivity mActivity;

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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(LogoutEvent event) {
        AuthUI.getInstance().signOut(mActivity);
    }
}
