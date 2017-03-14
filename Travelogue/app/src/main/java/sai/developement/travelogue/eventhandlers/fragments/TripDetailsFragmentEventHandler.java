package sai.developement.travelogue.eventhandlers.fragments;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sai.developement.travelogue.events.ConnectivityStateChangeEvent;
import sai.developement.travelogue.fragments.TripDetailsFragment;

/**
 * Created by sai on 3/14/17.
 */

public class TripDetailsFragmentEventHandler extends AbstractFragmentEventHandler{


    public TripDetailsFragmentEventHandler(Fragment fragment) {
        super(fragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectivityStateChangeEvent event) {
        ((TripDetailsFragment) mFragment).toggleBehavior(event.mIsConnected);
    }
}
