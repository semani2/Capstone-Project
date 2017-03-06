package sai.developement.travelogue.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/5/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.TripViewHolder> {

    private final ArrayList<Trip> mTripsLit;
    private final Activity mActivity;

    public TripsRecyclerAdapter(ArrayList<Trip> trips, Activity activity) {
        mTripsLit = trips;
        mActivity = activity;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item_layout, parent, false);

        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, final int position) {
        holder.mTripNameTextView.setText(mTripsLit.get(position).getName());
        holder.mDateTextView.setText(mTripsLit.get(position).getStartDate() + " - "
                + mTripsLit.get(position).getEndDate());
        holder.mTripCreatorTextView.setText(mTripsLit.get(position).getCreatedByUsername());
        holder.mTripLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putParcelable(ViewTripActivity.TRIP_KEY, mTripsLit.get(position));

                Intent intent = new Intent(mActivity, ViewTripActivity.class);
                intent.putExtras(extras);

                mActivity.startActivity(intent);
            }
        });
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
        public CardView mTripLayout;

        public TripViewHolder(View v) {
            super(v);
            mTripNameTextView = (TextView) v.findViewById(R.id.trip_name_text_view);
            mDateTextView = (TextView) v.findViewById(R.id.trip_date_text_view);
            mTripCreatorTextView = (TextView) v.findViewById(R.id.trip_creator_text_view);
            mTripLayout = (CardView) v.findViewById(R.id.trip_layout);
        }
    }
}
