package sai.developement.travelogue.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.fragments.DayFragment;
import sai.developement.travelogue.fragments.TripDetailsFragment;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.TripDay;

/**
 * Created by sai on 3/5/17.
 */

public class TripPagesAdapter extends FragmentStatePagerAdapter {

    private final Trip mTrip;
    private final List<TripDay> mTripDays;
    private final Context mContext;

    public TripPagesAdapter(Trip trip, List<TripDay> tripDays, Context context, FragmentManager fm) {
        super(fm);
        this.mTrip = trip;
        this.mTripDays = tripDays;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle extras = new Bundle();
        extras.putParcelable(ViewTripActivity.TRIP_KEY, mTrip);
        if(position == 0) {
            fragment = new TripDetailsFragment();
        }
        else {
            fragment = new DayFragment();
        }
        fragment.setArguments(extras);
        return fragment;

    }

    @Override
    public int getCount() {
        return mTrip.getDuration();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return mContext.getString(R.string.str_trip_details);
        }
        return mContext.getString(R.string.str_day, String.valueOf(position));
    }
}
