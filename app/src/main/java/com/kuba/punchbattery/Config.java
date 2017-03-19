package com.kuba.punchbattery;

/**
 * Created by Kuba on 2017-03-09.
 */

public class Config {
    public static String batteryLogFile = "batteryLog.dat";
    public static int maxLogFileLength = 350; // maksymalna ilosc linijek w pliku, potem kasujemy
    public static int waitBetweenDataCollections = 10000; // w milisekundach

    public static Pattern currentPattern;
}
