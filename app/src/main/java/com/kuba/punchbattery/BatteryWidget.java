package com.kuba.punchbattery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.widget.RemoteViews;


public class BatteryWidget extends AppWidgetProvider {

    public BatteryData current;
    private static final String ACTION_BATTERY_UPDATE = "com.kuba.punchbattery.action.Update";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        current = BatteryData.getCurrent(context);
        //updateViews(context);

        //turnAlarmOnOff(context, true);
        context.startService(new Intent(context, ScreenMonitorService.class));
    }

    //// metody pomocnicze

    // wybiera obrazek do poziomu baterii

    public static void turnAlarmOnOff(Context context, boolean turnOn) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_BATTERY_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if (turnOn) { // Add extra 1 sec because sometimes ACTION_BATTERY_CHANGED is called after the first alarm
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 300 * 1000, pendingIntent);

        } else {
            alarmManager.cancel(pendingIntent);

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        current = BatteryData.getCurrent(context);
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
            views.setTextViewText(R.id.batteryText, current.level + "%");
            views.setImageViewResource(R.id.dzialajKurwiu, Config.currentPattern.chooseImageResource(current));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private boolean batteryChanged(BatteryData newData) {
        return (current != newData);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_BATTERY_UPDATE)) {
            BatteryData newData = BatteryData.getCurrent(context);
            if (batteryChanged(newData)) {
                current = newData;
                //updateViews(context);
            }
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        turnAlarmOnOff(context, false);
        context.stopService(new Intent(context, ScreenMonitorService.class));
    }
}


