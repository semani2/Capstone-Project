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
        View view = convertView;
        ViewHolder viewHolder;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.avatar_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.avatarImageView.setImageResource(Constants.AVATARS.get(position + 1));
        return view;
    }

    private static class ViewHolder {
        ImageView avatarImageView;
    }
}
