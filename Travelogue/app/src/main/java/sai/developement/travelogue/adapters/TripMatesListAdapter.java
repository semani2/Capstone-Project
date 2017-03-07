package sai.developement.travelogue.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import sai.developement.travelogue.R;
import sai.developement.travelogue.models.User;

/**
 * Created by sai on 3/2/17.
 */

public class TripMatesListAdapter extends ArrayAdapter<User> {

    private final Context mContext;

    public TripMatesListAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user =  getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_mates_list_item, parent, false);
        }

        ImageView profileImageView = (ImageView) convertView.findViewById(R.id.user_profile_image_view);
        TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name_text_view);
        TextView userEmailTextView = (TextView) convertView.findViewById(R.id.user_email_text_view);
        Glide.with(mContext)
                .load(Uri.parse("https://randomuser.me/api/portraits/lego/"+getRandomImageId()+".jpg"))
                .into(profileImageView);
        userNameTextView.setText(user.getName());
        userEmailTextView.setText(user.getEmail());

        return convertView;
    }

    private int getRandomImageId() {
        Random random = new Random();
        return random.nextInt(8) + 1;
    }
}
