package com.kuba.punchbattery;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Config aplikacji
 */

public class Config {
    //// od razu wpisywać defaultowe wartości - przy błędzie zostaną takie
    public static String batteryLogFile = "batteryLog.dat";
    public static int maxLogFileLength = 350; // maksymalna ilosc linijek w pliku, potem kasujemy
    public static int waitBetweenDataCollections = 10000; // w milisekundach
    public static int currentPatternId = 0;
    public static ArrayList<String> plugins = new ArrayList<>();
    private static String sharedPreferencesName = "global_settings";
    //// klucze w sharedpreferences
    private static String checkKey = "check";
    private static String batteryLogFileKey = "battery_log_file";
    private static String maxLogFileLengthKey = "max_log_file_length";
    private static String waitBetweenDataCollectionsKey = "wait_between_data_collections";
    private static String currentPatternIdKey = "current_pattern_id";
    private static String pluginsKey = "saved_plugins"; // format: nazwy pakietów rozdzielone średnikami np. com.kuba.punchbattery.jebanyplugin;com.kuba.innyskurwesyn
    private static boolean loaded = false;
    public static boolean load(Context c) // upewnia się czy wartości są już załadowane, zwraca false jeśli wartości już załadowane
    {
        if(!loaded)
        {
            Context appContext = c.getApplicationContext(); // używać zawsze ten sam kontekst aplikacji
            SharedPreferences pref = appContext.getSharedPreferences(sharedPreferencesName, 0);
            if (!pref.contains(checkKey))
                // pierwsze uruchomienie, brak preferences, utworzyć
                save(c);
            else {
                batteryLogFile = pref.getString(batteryLogFileKey, null);
                maxLogFileLength = pref.getInt(maxLogFileLengthKey, 0);
                waitBetweenDataCollections = pref.getInt(waitBetweenDataCollectionsKey, 0);
                currentPatternId = pref.getInt(currentPatternIdKey, 0);
                plugins.clear();
                String[] savedPluginsStr = pref.getString(pluginsKey, null).split(";");
                for (String s : savedPluginsStr)
                    plugins.add(s);
            }
            return true;
        }
        else
            return false;
    }

    public static void save(Context c) {
        Context appContext = c.getApplicationContext(); // używać zawsze ten sam kontekst aplikacji
        SharedPreferences pref = appContext.getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString(batteryLogFileKey, batteryLogFile);
        edit.putBoolean(checkKey, true);
        edit.putInt(maxLogFileLengthKey, maxLogFileLength);
        edit.putInt(waitBetweenDataCollectionsKey, waitBetweenDataCollections);
        edit.putInt(currentPatternIdKey, currentPatternId);
        StringBuilder sb = new StringBuilder();
        for (String s : plugins)
            sb.append(s).append(';');
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1); // żeby usunąć ostatni średnik
        edit.putString(pluginsKey, sb.toString());

        edit.apply();
    }
}
