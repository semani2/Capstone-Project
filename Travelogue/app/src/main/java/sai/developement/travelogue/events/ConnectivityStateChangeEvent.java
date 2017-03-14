package sai.developement.travelogue.events;

/**
 * Created by sai on 3/14/17.
 */

public class ConnectivityStateChangeEvent {

    public final boolean mIsConnected;

    public ConnectivityStateChangeEvent(boolean isConnected) {
        mIsConnected = isConnected;
    }
}
