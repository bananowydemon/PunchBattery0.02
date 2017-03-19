package com.kuba.punchbattery;

import android.content.Context;

/**
 * Zmienne uzywane przez wszystkie klasy ładowane globalnie
 * !!! po zmianie Configu wywołać reloadValues
 */

public class Global {
    public static Pattern currentPattern;

    public static void reloadValues(Context c)
    {
        Context appContext = c.getApplicationContext();
        currentPattern = Pattern.get(appContext, Config.currentPatternId);
    }
}
