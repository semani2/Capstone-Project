package sai.developement.travelogue.eventhandlers.fragments;

import sai.developement.travelogue.events.ConnectivityStateChangeEvent;

/**
 * Created by sai on 3/14/17.
 */

public interface IFragmentEventHandler {

    void onStart();

    void onStop();

    void onEventMainThread(ConnectivityStateChangeEvent event);
}
