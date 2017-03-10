package sai.developement.travelogue.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.fragments.TripsFragment;

/**
 * Created by sai on 3/10/17.
 */

public class HomePagesAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final String mCurrentUserId;

    public HomePagesAdapter(FragmentManager fm, Context context, String userId) {
        super(fm);
        this.mContext = context;
        this.mCurrentUserId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle extras = new Bundle();
        extras.putString(HomeActivity.USER_ID_KEY, mCurrentUserId);
        Fragment fragment = new TripsFragment();

        if(position == 0) {
            extras.putInt(HomeActivity.TRIP_FLAG, HomeActivity.MY_TRIPS_FLAG);
        }
        else {
            extras.putInt(HomeActivity.TRIP_FLAG, HomeActivity.SHARED_TRIPS_FLAG);
        }
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return mContext.getString(R.string.str_my_trips);
        }
        return mContext.getString(R.string.str_shared_trips);
    }
}
