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
import android.widget.RemoteViews;
import android.widget.Toast;


public class BatteryWidget extends AppWidgetProvider {
    private static final String ACTION_BATTERY_UPDATE = "com.kuba.punchbattery.action.Update";
    public  int batteryLevel = 0;

    public  int getBatteryLevel(){
        return batteryLevel;
    }



    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);



        turnAlarmOnOff(context, true);
        context.startService(new Intent(context, ScreenMonitorService.class));
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



        // Sometimes when the phone is booting, onUpdate method gets called before onEnabled()
        int currentLevel = calculateBatteryLevel(context);
        if (batteryChanged(currentLevel)) {
            batteryLevel = currentLevel;


            for(int j = 0; j < appWidgetIds.length; j++)
            {
                int appWidgetId = appWidgetIds[j];

                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    //i/I(?)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getName()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            context, 0, intent, 0);
                    RemoteViews views = new RemoteViews(context.getPackageName(),
                            R.layout.battery_widget);
                    RemoteViews views1 = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
                    if (batteryLevel > 66){
                        views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt);

                    }else if (batteryLevel < 66 && batteryLevel > 30){
                        views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt1);

                    }else{
                        views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt2);
                    }
                    views.setOnClickPendingIntent(R.id.dzialajKurwiu, pendingIntent);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    appWidgetManager.updateAppWidget(appWidgetId, views1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context.getApplicationContext(),
                            "There was a problem loading the application: ",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
        updateViews(context);
    }

    private boolean batteryChanged(int currentLevelLeft) {
        return (batteryLevel != currentLevelLeft);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);



        if (intent.getAction().equals(ACTION_BATTERY_UPDATE)) {
            int currentLevel = calculateBatteryLevel(context);
            if (batteryChanged(currentLevel)) {
                batteryLevel = currentLevel;
                updateViews(context);
            }
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);



        turnAlarmOnOff(context, false);
        context.stopService(new Intent(context, ScreenMonitorService.class));
    }

    private int calculateBatteryLevel(Context context) {


        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        return level * 100 / scale;
    }

    private void updateViews(Context context) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
        views.setTextViewText(R.id.batteryText, batteryLevel + "%");

        ComponentName componentName = new ComponentName(context, BatteryWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName, views);


        RemoteViews views1 = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
        if (batteryLevel > 66){
            views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt);

        }else if (batteryLevel < 66 && batteryLevel > 33){
            views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt1);

        }else if (batteryLevel > 33){
            views1.setImageViewResource(R.id.dzialajKurwiu, R.drawable.batt2);
        }

    }

}


