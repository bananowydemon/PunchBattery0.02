package com.kuba.punchbattery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


public class OnBootReceiver extends BroadcastReceiver {
    private int waitBetweenDataCollections = 600000; // w milisekundach
    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DataCollector.turnAlarmOnOff(context, true, true);
    }
}
