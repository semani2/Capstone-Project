package sai.developement.travelogue.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.fragments.DayFragment;
import sai.developement.travelogue.fragments.TripDetailsFragment;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/5/17.
 */

public class TripPagesAdapter extends FragmentStatePagerAdapter {

    private final Trip mTrip;
    private final Context mContext;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    public TripPagesAdapter(Trip trip, Context context, FragmentManager fm) {
        super(fm);
        this.mTrip = trip;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if(position == 0) {
            fragment = new TripDetailsFragment();
            Bundle extras = new Bundle();
            extras.putParcelable(ViewTripActivity.TRIP_KEY, mTrip);
            fragment.setArguments(extras);
        }
        else {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(mTrip.getStartDateMillis());
            startCalendar.add(Calendar.DATE, position - 1);
            fragment = DayFragment.newInstance(position, dateFormatForMonth.format(startCalendar.getTime()), mTrip.getId());
        }

        return fragment;

    }

    @Override
    public int getCount() {
        return mTrip.getDuration() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return mContext.getString(R.string.str_trip_details);
        }
        return mContext.getString(R.string.str_day, String.valueOf(position));
    }
}
