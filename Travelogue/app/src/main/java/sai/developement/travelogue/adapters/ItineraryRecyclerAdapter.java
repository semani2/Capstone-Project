package sai.developement.travelogue.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
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

import java.util.List;

import sai.developement.travelogue.R;
import sai.developement.travelogue.models.TripVisit;

/**
 * Created by sai on 3/6/17.
 */

public class ItineraryRecyclerAdapter extends RecyclerView.Adapter<ItineraryRecyclerAdapter.ItineraryViewHolder> {

    private final Context mContext;
    private final List<TripVisit> mTripVisits;

    public ItineraryRecyclerAdapter(Context context, List<TripVisit> tripVisits) {
        this.mContext = context;
        this.mTripVisits = tripVisits;
    }

    @Override
    public ItineraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_itinerary_item, parent, false);

        return new ItineraryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItineraryViewHolder holder, int position) {
        holder.mItiPlaceTextView.setText(mTripVisits.get(position).getPlace());
        holder.mItiLocationTextView.setText(mTripVisits.get(position).getLocation());
        if(mTripVisits.get(position).getTripTime() != null) {
            holder.mItiTimeTextView.setText(mTripVisits.get(position).getTripTime());
        }

        if(mTripVisits.get(position).getImageUrl() != null) {
            Glide.with(mContext)
                    .load(mTripVisits.get(position).getImageUrl())
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            onPalette(Palette.from(resource).generate());
                            holder.mItiImageView.setImageBitmap(resource);

                            return false;
                        }

                        public void onPalette(Palette palette) {
                            if (null != palette) {
                                holder.mItiTextLayout.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
                            }
                        }
                    })
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.mItiImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mTripVisits.size();
    }

    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mItiPlaceTextView;
        public TextView mItiLocationTextView;
        public TextView mItiTimeTextView;
        public ImageView mItiImageView;
        public LinearLayout mItiTextLayout;

        public ItineraryViewHolder(View v) {
            super(v);
            mItiPlaceTextView = (TextView) v.findViewById(R.id.iti_place_text_view);
            mItiLocationTextView = (TextView) v.findViewById(R.id.iti_location_text_view);
            mItiTimeTextView = (TextView) v.findViewById(R.id.iti_time_text_view);
            mItiImageView = (ImageView) v.findViewById(R.id.iti_image_view);
            mItiTextLayout = (LinearLayout) v.findViewById(R.id.suggestion_text_layout);
        }
    }
}
