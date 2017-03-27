package com.kuba.punchbattery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * Klasa pobiera dane o telefonie i zapisuje w plikach oraz kontroluje rozmiar plikow
 *
 * Odpalenie serwisu, np. przez mainactivity, lub podczas dodawania widgetu
 *
 *         Intent mServiceIntent = new Intent(MainActivity.this, DataCollector.class);
 *         mServiceIntent.putExtra("collectBattery", true);
 *         MainActivity.this.startService(mServiceIntent);
 *
 *!!!!!!!!!!!!!!  uzyc job scheduler api, lub alarmmenager do tej klasy, zamiast while(true),  bo moze sie wywalac !!!!!!!!!!!
 */
public class DataCollector extends IntentService {
    public static String COLLECT_BATTERY = "collectBattery";
    private static long numberOfonHandleIntentExecutions = 0; // ta zminna zlicza ilosc zapisow w glownej metodzie, co n uruchomien przepriowadzane jest czyszczenie plikow
    private boolean collectBattery = true; // czy zbierac dane o baterii? do kazdej opcji taka zmienna
    //private int waitBetweenDataCollections = 600000; // w milisekundach
    private long fileSizeControlEveryNRuns = 50; // co ile zapisow ma byc sprawdzany rozmiar pliku i skracany

    public DataCollector() {
        super("DataCollector");
    }

    public static void turnAlarmOnOff(Context con, boolean onOff, boolean collectBattery) {
        Intent intent = new Intent(con, DataCollector.class);
        //intent.putExtra(DataCollector.COLLECT_BATTERY, true);
        PendingIntent pendingIntent = PendingIntent.getService(con, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, Config.waitBetweenDataCollections, pendingIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) { // glowna metoda wywolywana przy uruchomieniu serwisu
        if (intent != null) {
            whatToMonitor (intent);
            /*Calendar calendar;
            while (true) { // petla nieskonczona

                calendar = Calendar.getInstance();
                long milisecondsAtBeginning = calendar.getTimeInMillis(); // czas w milisekundach po rozpoczeciu petli

                if (collectBattery) { // zbieranie info o baterii
                    String batteryLevel = Integer.toString(calculateBatteryLevel(getApplicationContext()));
                    LogFile.log(batteryLevel, this.batteryLogName);
                    if (numberOfonHandleIntentExecutions % fileSizeControlEveryNRuns == 0) { //co ile kontrola rozmiaru plikow
                        LogFile.fileSizeControl(this.maxLogFileLength, this.batteryLogName);
                    }
                }
                this.numberOfonHandleIntentExecutions++;

                calendar = Calendar.getInstance();
                long milisecondsAtEnd = calendar.getTimeInMillis(); // czas w milisekundach po zakonczeniu petli

                try {
                    Thread.sleep(waitBetweenDataCollections - (milisecondsAtEnd - milisecondsAtBeginning));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            if (collectBattery) { // zbieranie info o baterii
                LogFile.log(getApplicationContext(), BatteryData.getCurrent(this).toString(), Config.batteryLogFile);
                if (DataCollector.numberOfonHandleIntentExecutions % fileSizeControlEveryNRuns == 0) { //co ile kontrola rozmiaru plikow
                    LogFile.fileSizeControl(getApplicationContext(), Config.maxLogFileLength, Config.batteryLogFile);
                }
            }
            DataCollector.numberOfonHandleIntentExecutions++;
        }
    }

    private void whatToMonitor (Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(COLLECT_BATTERY)) {
                collectBattery = extras.getBoolean(COLLECT_BATTERY, false);
            }
        }
    }
    
}
