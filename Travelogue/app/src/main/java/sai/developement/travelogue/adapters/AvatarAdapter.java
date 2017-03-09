package sai.developement.travelogue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import sai.developement.travelogue.Constants;
import sai.developement.travelogue.R;

/**
 * Created by sai on 3/8/17.
 */

public class AvatarAdapter extends BaseAdapter {
    private final Context mContext;

    public AvatarAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return Constants.AVATARS.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;

        ImageView avatarImageView;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.avatar_item_layout, null);
        avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
        /*Glide.with(mContext)
                .load()
                .centerCrop()
                .into(avatarImageView);*/
        avatarImageView.setImageResource(Constants.AVATARS.get(position + 1));
        return view;
    }
}
