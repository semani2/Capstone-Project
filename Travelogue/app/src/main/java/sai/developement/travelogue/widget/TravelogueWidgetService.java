package sai.developement.travelogue.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sai.developement.travelogue.CurrentUser;
import sai.developement.travelogue.R;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/13/17.
 */

public class TravelogueWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TravelogueRemoteViewsFactory(getApplicationContext());
    }

    class TravelogueRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;

        private List<Trip> mTripsList = new ArrayList<>();

        DatabaseReference mTripsDatabaseReference;

        private boolean firstUpdate = false;

        public TravelogueRemoteViewsFactory(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {
            mTripsDatabaseReference = FirebaseDatabaseHelper.getTripsDatabaseReference();
            fetchTripsForUser();
        }

        @Override
        public void onDataSetChanged() {
            fetchTripsForUser();
        }

        private void fetchTripsForUser() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String userId = sharedPreferences.getString(CurrentUser.USER_ID_KEY, null);

            if(userId == null) return;

            mTripsDatabaseReference.child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mTripsList.clear();
                            for(DataSnapshot tripData : dataSnapshot.getChildren()) {
                                mTripsList.add(tripData.getValue(Trip.class));
                            }
                            updateWidget();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        private void updateWidget() {
            if(firstUpdate) return;

            firstUpdate = true;

            ComponentName name = new ComponentName(mContext, TravelogueWidgetProvider.class);
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(mContext);
            int[] ids = widgetManager.getAppWidgetIds(name);
            Intent intent = new Intent(mContext, TravelogueWidgetProvider.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            intent.putExtra(AppWidgetManager.ACTION_APPWIDGET_UPDATE, ids);

            widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);

            mContext.sendBroadcast(intent);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return mTripsList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(position >= mTripsList.size()) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            Bundle extras = new Bundle();
            extras.putParcelable(TravelogueWidgetProvider.TRIP_ITEM, mTripsList.get(position));

            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent);

            String tripName = mTripsList.get(position).getName();
            String tripDate = mTripsList.get(position).getStartDate() + " - " + mTripsList.get(position).getEndDate();

            views.setTextViewText(R.id.widget_trip_name_text_view, tripName);
            views.setTextViewText(R.id.widget_trip_date_text_view, tripDate);
            try {
                Bitmap bitmap = Glide.with(mContext)
                        .load(mTripsList.get(position).getPhotoUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(50, 50)
                        .get();
                views.setImageViewBitmap(R.id.widget_trip_image_view, bitmap);
            }
            catch (Exception e) {
                views.setImageViewResource(R.id.widget_trip_image_view, R.drawable.travelogue_icon);
            }

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
