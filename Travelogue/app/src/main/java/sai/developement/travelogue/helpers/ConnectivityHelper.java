package sai.developement.travelogue.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import org.greenrobot.eventbus.EventBus;

import sai.developement.travelogue.events.ConnectivityStateChangeEvent;

/**
 * Created by sai on 3/14/17.
 */

public class ConnectivityHelper extends ConnectivityManager.NetworkCallback {

    private Context mContext;

    final NetworkRequest networkRequest;

    public ConnectivityHelper(Context context) {
        mContext = context;
        networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
    }

    public void init() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        EventBus.getDefault().post(new ConnectivityStateChangeEvent(true));
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        EventBus.getDefault().post(new ConnectivityStateChangeEvent(false));
    }
}
