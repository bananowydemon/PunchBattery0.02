package com.kuba.punchbattery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

/**
 * Zajmuje się odczytywaniem danych baterii
 */

public class BatteryData {
    public int level;
    public int temperature;
    public boolean plugged;

    public static BatteryData getCurrent(Context context) {
        BatteryData data = new BatteryData();
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        data.temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        data.plugged = (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0);
        data.level = level * 100 / scale;
        return data;
    }

    // TODO: nie sprawdza czy poprawny format danych, powinno rzucać wyjątek ale chujowo się go łapie w GraphActivity
    public static BatteryData fromString(String s) {
        BatteryData data = new BatteryData();
        String[] strings = s.split(";");
        //if(strings.length != 3)
        //    throw new IOException("Invalid battery data format");
        data.level = Integer.parseInt(strings[0]);
        data.temperature = Integer.parseInt(strings[1]);
        data.plugged = Boolean.parseBoolean(strings[2]);
        return data;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(level).append(';').append(temperature).append(';').append(plugged);
        return sb.toString();
    }
}