package sai.developement.travelogue.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import sai.developement.travelogue.R;

/**
 * Created by sai on 3/13/17.
 */

public class TravelogueWidgetProvider extends AppWidgetProvider {

    public static final String TRIP_ACTION = "sai.developement.travelogue.widget.TRIP_ACTION";
    public static final String OPEN_ACTION = "sai.developement.travelogue.widget.OPEN_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {


            Intent intent = new Intent(context, TravelogueWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.travelogue_widget_layout);

            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, intent);

            rv.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
