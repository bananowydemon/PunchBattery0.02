package com.kuba.punchbattery;

import android.content.Context;

/**
 * Config aplikacji
 * TODO: ładować z SharedPreferences
 */

public class Config {
    public static String batteryLogFile = "batteryLog.dat";
    public static int maxLogFileLength = 350; // maksymalna ilosc linijek w pliku, potem kasujemy
    public static int waitBetweenDataCollections = 10000; // w milisekundach
    public static int currentPatternId;

    private static boolean loaded = false;
    public static boolean load(Context c) // upewnia się czy wartości są już załadowane, zwraca false jeśli wartości już załadowane
    {
        Context appContext = c.getApplicationContext(); // używać zawsze ten sam kontekst aplikacji
        if(!loaded)
        {
            currentPatternId = 0;
            return true;
        }
        else
            return false;
    }
}
