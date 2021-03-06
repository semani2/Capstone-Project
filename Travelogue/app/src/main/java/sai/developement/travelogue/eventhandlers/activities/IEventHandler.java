package sai.developement.travelogue.eventhandlers.activities;

import sai.developement.travelogue.events.AuthStateChangeEvent;
import sai.developement.travelogue.events.ConnectivityStateChangeEvent;
import sai.developement.travelogue.events.LogoutEvent;
import sai.developement.travelogue.events.SelectAvatarEvent;
import sai.developement.travelogue.events.ShowMessageEvent;

/**
 * Created by sai on 3/9/17.
 */

public interface IEventHandler {

    void onPause();

    void onResume();

    void onEventMainThread(AuthStateChangeEvent event);

    void onEventMainThread(SelectAvatarEvent event);

    void onEventMainThread(LogoutEvent event);

    void onEventMainThread(ShowMessageEvent event);

    void onEventMainThread(ConnectivityStateChangeEvent event);

}
