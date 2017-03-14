package sai.developement.travelogue.eventhandlers.activities;

import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.activities.TravelogueActivity;
import sai.developement.travelogue.eventhandlers.activities.AbstractEventHandler;
import sai.developement.travelogue.events.ConnectivityStateChangeEvent;

/**
 * Created by sai on 3/9/17.
 */

public class HomeEventHandler extends AbstractEventHandler {

    public HomeEventHandler(TravelogueActivity travelogueActivity) {
        super(travelogueActivity);
    }

    @Override
    public void onEventMainThread(ConnectivityStateChangeEvent event) {
        super.onEventMainThread(event);
        ( (HomeActivity) mActivity).toggleFAB(event.mIsConnected);
    }
}
