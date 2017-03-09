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
import sai.developement.travelogue.models.Suggestion;

/**
 * Created by sai on 3/7/17.
 */

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionsViewHolder>{

    private final List<Suggestion> mSuggestionList;

    private final Context mContext;

    public SuggestionsAdapter(List<Suggestion> suggestionList, Context context) {
        this.mSuggestionList = suggestionList;
        this.mContext = context;
    }

    @Override
    public SuggestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_item, parent, false);

        return new SuggestionsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuggestionsViewHolder holder, int position) {
        holder.mSuggestionNameTextView.setText(mSuggestionList.get(position).getName());
        holder.mSuggestionAddressTextView.setText(mSuggestionList.get(position).getAddress());

        if(mSuggestionList.get(position).getPhotoUrl() != null) {
            Glide.with(mContext)
                    .load(mSuggestionList.get(position).getPhotoUrl())
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            onPalette(Palette.from(resource).generate());
                            holder.mSuggestionsImageView.setImageBitmap(resource);

                            return false;
                        }

                        public void onPalette(Palette palette) {
                            if (null != palette) {
                                holder.mSuggestionTextLayout.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
                            }
                        }
                    })
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.mSuggestionsImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mSuggestionList.size();
    }

    public static class SuggestionsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mSuggestionNameTextView;
        public TextView mSuggestionAddressTextView;
        public ImageView mSuggestionsImageView;
        public LinearLayout mSuggestionTextLayout;

        public SuggestionsViewHolder(View v) {
            super(v);
            mSuggestionNameTextView = (TextView) v.findViewById(R.id.suggestion_name_text_view);
            mSuggestionAddressTextView = (TextView) v.findViewById(R.id.suggestion_address_text_view);
            mSuggestionsImageView = (ImageView) v.findViewById(R.id.suggestions_image_view);
            mSuggestionTextLayout = (LinearLayout) v.findViewById(R.id.suggestion_text_layout);
        }
    }
}
