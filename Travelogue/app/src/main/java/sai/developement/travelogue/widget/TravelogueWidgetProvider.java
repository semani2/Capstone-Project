package sai.developement.travelogue.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import sai.developement.travelogue.R;
import sai.developement.travelogue.activities.HomeActivity;
import sai.developement.travelogue.activities.ViewTripActivity;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/13/17.
 */

public class TravelogueWidgetProvider extends AppWidgetProvider {

    public static final String TRIP_ACTION = "sai.developement.travelogue.widget.TRIP_ACTION";
    public static final String OPEN_ACTION = "sai.developement.travelogue.widget.OPEN_ACTION";
    public static final String TRIP_ITEM = "sai.developement.travelogue.widget.TRIP_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {


            Intent intent = new Intent(context, TravelogueWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.travelogue_widget_layout);

            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, intent);

            rv.setOnClickPendingIntent(R.id.widget_titlebar_layout, getAppOpenIntent(context));

            Intent viewTripIntent = new Intent(context, TravelogueWidgetProvider.class);
            viewTripIntent.setAction(TravelogueWidgetProvider.TRIP_ACTION);
            viewTripIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent viewTripPendingIntent = PendingIntent.getBroadcast(context, 0,
                    viewTripIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list_view, viewTripPendingIntent);

            rv.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(TRIP_ACTION)) {
            Bundle inExtras = intent.getExtras();
            Trip viewTrip = inExtras.getParcelable(TRIP_ITEM);

            Intent viewTripIntent = new Intent(context, ViewTripActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable(ViewTripActivity.TRIP_KEY, viewTrip);
            viewTripIntent.putExtras(extras);

            context.startActivity(viewTripIntent);
        }

        if(intent.getAction() != null && intent.getAction().equals(OPEN_ACTION)) {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(homeIntent);
        }
        super.onReceive(context, intent);
    }

    private PendingIntent getAppOpenIntent(Context context) {
        Intent intent = new Intent(context, TravelogueWidgetProvider.class);
        intent.setAction(OPEN_ACTION);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
