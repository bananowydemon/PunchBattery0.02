package com.kuba.punchbattery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.widget.RemoteViews;
import android.widget.Toast;


public class BatteryWidget extends AppWidgetProvider {

    public static class Data
    {
        public int level;
        public int temperature;
        public boolean plugged;
    }

    public Data current;
    private static final String ACTION_BATTERY_UPDATE = "com.kuba.punchbattery.action.Update";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        current = getBatteryData(context);
        //updateViews(context);

        //turnAlarmOnOff(context, true);
        context.startService(new Intent(context, ScreenMonitorService.class));
    }

    //// metody pomocnicze
    public static Data getBatteryData(Context context) {
        Data data = new Data();
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        data.temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        data.plugged = (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0);
        data.level = level * 100 / scale;
        return data;
    }

    // wybiera obrazek do poziomu baterii
    public static int chooseBatteryResource(int level)
    {
        if (level > 66) return R.drawable.batt;
        if (level > 30) return R.drawable.batt1;
        return R.drawable.batt2;
    }

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

        current = getBatteryData(context);
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
            views.setTextViewText(R.id.batteryText, current.level + "%");
            views.setImageViewResource(R.id.dzialajKurwiu, chooseBatteryResource(current.level));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private boolean batteryChanged(Data newData) {
        return (current != newData);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_BATTERY_UPDATE)) {
            Data newData = getBatteryData(context);
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


