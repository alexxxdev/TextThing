package click.dummer.textthing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.Calendar;

public class MyWidgetProvider extends AppWidgetProvider {
    public static final int LONG_UPDATE  = 30 * 60 * 1000;
    public static int width = 90;
    public static int height = 70;

    private PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar TIME = Calendar.getInstance();

        for (int i = 0; i < count; i++) {

            TIME.set(Calendar.MINUTE, 0);
            TIME.set(Calendar.SECOND, 0);
            TIME.set(Calendar.MILLISECOND, 0);
            Intent in = new Intent(context, WidgetUpdateService.class);

            if (service == null) {
                service = PendingIntent.getService(context, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);
            }

            m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), LONG_UPDATE, service);

            // ---------
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.retro_widget);
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.putExtra("width", width);
            intent.putExtra("height", height);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );
            remoteViews.setOnClickPendingIntent(R.id.wContentLayout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        height = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        int[] appWidgetIds = new int[1];
        appWidgetIds[0] = appWidgetId;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.retro_widget);
        Intent intent = new Intent(context, MyWidgetProvider.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra("width", width);
        intent.putExtra("height", height);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.wContentLayout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        m.cancel(service);
    }
}
