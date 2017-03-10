package sai.developement.travelogue.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import sai.developement.travelogue.Constants;
import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.R;
import sai.developement.travelogue.models.TripMessage;

/**
 * Created by sai on 3/9/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MESSAGE_SELF = 0;
    public static final int MESSAGE_OTHER = 1;

    private final Context mContext;
    private final List<TripMessage> mTripMessages;

    public ChatAdapter(Context context, List<TripMessage> tripMessages) {
        mContext = context;
        mTripMessages = tripMessages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_SELF) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_self, parent, false);
            return new ChatSelfViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_other, parent, false);
            return new ChatOtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if(itemType == MESSAGE_SELF) {
            ChatSelfViewHolder viewHolder = (ChatSelfViewHolder)holder;
            Glide.with(mContext)
                    .load(Constants.AVATARS.get(CurrentUser.getCurrentuser().getUserAvatarId()))
                    .into(viewHolder.mAvatarImageView);
            viewHolder.mMsgTextView.setText(mTripMessages.get(position).getMessage());
            viewHolder.mMsgSentTextView.setText(mTripMessages.get(position).getSentTime());
        }
        else {
            ChatOtherViewHolder viewHolder = (ChatOtherViewHolder) holder;
            Glide.with(mContext)
                    .load(Constants.AVATARS.get(mTripMessages.get(position).getUserAvatarId()))
                    .into(viewHolder.mAvatarImageView);
            viewHolder.mMsgTextView.setText(mTripMessages.get(position).getMessage());
            viewHolder.mMsgSentTextView.setText(mTripMessages.get(position).getSentTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        TripMessage tripMessage = mTripMessages.get(position);
        if(tripMessage.getUserId().equalsIgnoreCase(CurrentUser.getCurrentuser().getUserId())) {
            return MESSAGE_SELF;
        }
        return MESSAGE_OTHER;
    }

    @Override
    public int getItemCount() {
        return mTripMessages.size();
    }

    public static class ChatOtherViewHolder extends RecyclerView.ViewHolder {
        public TextView mMsgTextView;
        public TextView mMsgSentTextView;
        public ImageView mAvatarImageView;
        public CardView mMsgCardView;

        public ChatOtherViewHolder(View v) {
            super(v);
            mMsgTextView = (TextView) v.findViewById(R.id.message_text_view);
            mMsgSentTextView = (TextView) v.findViewById(R.id.message_time_text_view);
            mAvatarImageView = (ImageView) v.findViewById(R.id.message_avatar_image_view);
            mMsgCardView = (CardView) v.findViewById(R.id.chat_message_card_view);
        }
    }

    public static class ChatSelfViewHolder extends RecyclerView.ViewHolder {
        public TextView mMsgTextView;
        public TextView mMsgSentTextView;
        public ImageView mAvatarImageView;
        public CardView mMsgCardView;

        public ChatSelfViewHolder(View v) {
            super(v);
            mMsgTextView = (TextView) v.findViewById(R.id.message_text_view);
            mMsgSentTextView = (TextView) v.findViewById(R.id.message_time_text_view);
            mAvatarImageView = (ImageView) v.findViewById(R.id.message_avatar_image_view);
            mMsgCardView = (CardView) v.findViewById(R.id.chat_message_card_view);
        }
    }
}
