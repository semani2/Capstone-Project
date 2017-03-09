package sai.developement.travelogue.events;

/**
 * Created by sai on 3/9/17.
 */

/** Launch this event, to indicate authstate changes  **/
public class AuthStateChangeEvent {

    public final boolean isUserLoggedIn;

    public AuthStateChangeEvent(boolean isUserLoggedIn) {
        this.isUserLoggedIn = isUserLoggedIn;
    }
}
