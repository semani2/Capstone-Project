package sai.developement.travelogue.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sai.developement.travelogue.R;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/5/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.TripViewHolder> {

    private final ArrayList<Trip> mTripsLit;

    public TripsRecyclerAdapter(ArrayList<Trip> trips) {
        mTripsLit = trips;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item_layout, parent, false);

        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        holder.mTripNameTextView.setText(mTripsLit.get(position).getName());
        holder.mDateTextView.setText(mTripsLit.get(position).getStartDate() + " - "
                + mTripsLit.get(position).getEndDate());
        holder.mTripCreatorTextView.setText(mTripsLit.get(position).getCreatedByUsername());
    }

    @Override
    public int getItemCount() {
        return mTripsLit.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTripNameTextView;
        public TextView mDateTextView;
        public TextView mTripCreatorTextView;

        public TripViewHolder(View v) {
            super(v);
            mTripNameTextView = (TextView) v.findViewById(R.id.trip_name_text_view);
            mDateTextView = (TextView) v.findViewById(R.id.trip_date_text_view);
            mTripCreatorTextView = (TextView) v.findViewById(R.id.trip_creator_text_view);

        }
    }
}
