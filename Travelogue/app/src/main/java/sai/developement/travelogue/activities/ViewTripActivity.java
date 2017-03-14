package sai.developement.travelogue.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.TripPagesAdapter;
import sai.developement.travelogue.eventhandlers.IEventHandler;
import sai.developement.travelogue.eventhandlers.ViewTripEventHandler;
import sai.developement.travelogue.models.Trip;

public class ViewTripActivity extends TravelogueActivity {

    public static final String TRIP_KEY = "trip_key";

    private TripPagesAdapter mTripPagesAdapter;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    /*@BindView(R.id.trip_name_text_view)
    TextView tripNameTextView;

    @BindView(R.id.trip_loc_text_view)
    TextView tripLocTextView;*/

    @BindView(R.id.toolbar_image_view)
    ImageView tripImageView;

    private Trip mTrip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_trip);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() == null || getIntent().getExtras().getParcelable(TRIP_KEY) == null) {
            // No trip object
            goHome();
            return;
        }

        setSupportActionBar(mToolbar);

        mTrip = getIntent().getExtras().getParcelable(TRIP_KEY);

        mTripPagesAdapter = new TripPagesAdapter(mTrip, this, getSupportFragmentManager());

        mViewPager.setAdapter(mTripPagesAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mCollapsingToolbarLayout.setTitleEnabled(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTrip.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*tripNameTextView.setText(mTrip.getName());
        tripLocTextView.setText(mTrip.getPrimaryLocation());*/

        Glide.with(this)
                .load(mTrip.getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        changeToolbarColors(bitmap);
                        return false;
                    }
                })
                .into(tripImageView);
    }

    @Override
    public void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.chat:
                openChat();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openChat() {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(ChatActivity.TRIP_KEY, mTrip);

        chatIntent.putExtras(extras);
        startActivity(chatIntent);
    }

    private void changeToolbarColors(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
                int vibrantDarkColor = palette.getDarkVibrantColor(getResources().getColor(R.color.colorPrimaryDark));

                if (mCollapsingToolbarLayout != null) {
                    mCollapsingToolbarLayout.setContentScrimColor(vibrantColor);
                    mCollapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);
                }
            }
        });
    }


    @Override
    protected IEventHandler createEventHandler() {
        return new ViewTripEventHandler(this);
    }
}
