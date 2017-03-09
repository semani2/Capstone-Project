package sai.developement.travelogue.events;

/**
 * Created by sai on 3/9/17.
 */

public interface IEventHandler {

    void onEventMainThread(AuthStateChangeEvent event);

    void onEventMainThread(SelectAvatarEvent event);

    void onEventMainThread(LogoutEvent event);

}
