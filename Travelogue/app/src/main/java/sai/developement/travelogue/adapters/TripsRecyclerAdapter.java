package sai.developement.travelogue.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/5/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.TripViewHolder> {

    private final ArrayList<Trip> mTripsLit;
    private final Activity mActivity;
    private final int mTripFlag;
    private final Context mContext;

    public TripsRecyclerAdapter(Context context, ArrayList<Trip> trips, Activity activity, int tripFlag) {
        mTripsLit = trips;
        mActivity = activity;
        mTripFlag = tripFlag;
        mContext = context;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item_layout, parent, false);

        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, final int position) {
        holder.mTripNameTextView.setText(mTripsLit.get(position).getName());
        holder.mDateTextView.setText(mTripsLit.get(position).getStartDate() + " - "
                + mTripsLit.get(position).getEndDate());
        holder.mTripCreatorTextView.setText(mTripsLit.get(position).getCreatedByUsername());
        holder.mTripCreatorTextView.setVisibility(mTripFlag == HomeActivity.SHARED_TRIPS_FLAG ? View.VISIBLE : View.GONE);

        if(mTripsLit.get(position).getPhotoUrl() != null) {
            Glide.with(mContext)
                    .load(mTripsLit.get(position).getPhotoUrl())
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            onPalette(Palette.from(resource).generate());
                            holder.mTripImageView.setImageBitmap(resource);

                            return false;
                        }

                        public void onPalette(Palette palette) {
                            if (null != palette) {
                                holder.mTripTextLayout.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
                            }
                        }
                    })
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.mTripImageView);
        }

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
        public LinearLayout mTripTextLayout;
        public ImageView mTripImageView;

        public TripViewHolder(View v) {
            super(v);
            mTripNameTextView = (TextView) v.findViewById(R.id.trip_name_text_view);
            mDateTextView = (TextView) v.findViewById(R.id.trip_date_text_view);
            mTripCreatorTextView = (TextView) v.findViewById(R.id.trip_creator_text_view);
            mTripLayout = (CardView) v.findViewById(R.id.trip_layout);
            mTripTextLayout = (LinearLayout) v.findViewById(R.id.trip_text_layout);
            mTripImageView = (ImageView) v.findViewById(R.id.trip_image_view);
        }
    }
}
