package sai.developement.travelogue.eventhandlers.fragments;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sai.developement.travelogue.events.ConnectivityStateChangeEvent;
import sai.developement.travelogue.fragments.ChatFragment;

/**
 * Created by sai on 3/14/17.
 */

public class ChatFragmentEventHandler extends AbstractFragmentEventHandler {
    public ChatFragmentEventHandler(Fragment fragment) {
        super(fragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(ConnectivityStateChangeEvent event) {
        ((ChatFragment) mFragment).toggleBehavior(event.mIsConnected);
    }
}
