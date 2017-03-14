package sai.developement.travelogue.eventhandlers.fragments;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sai.developement.travelogue.events.ConnectivityStateChangeEvent;

/**
 * Created by sai on 3/14/17.
 */

public class AbstractFragmentEventHandler  implements IFragmentEventHandler{

    protected boolean isPaused = true;

    protected final Fragment mFragment;

    public AbstractFragmentEventHandler(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    public void onStart() {
        isPaused = false;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        isPaused = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(ConnectivityStateChangeEvent event) {

    }
}
