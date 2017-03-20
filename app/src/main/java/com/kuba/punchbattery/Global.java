package com.kuba.punchbattery;

import android.content.Context;

import java.util.ArrayList;

/**
 * Zmienne uzywane przez wszystkie klasy ładowane globalnie
 * !!! po zmianie Configu wywołać reloadValues
 */

public class Global {
    public static Pattern currentPattern;
    public static ArrayList<Pattern> patterns;

    public static void reloadValues(Context c)
    {
        Context appContext = c.getApplicationContext();
        currentPattern = Pattern.get(appContext, Config.currentPatternId);
        patterns = Pattern.getAllPatterns(c);
    }
}
