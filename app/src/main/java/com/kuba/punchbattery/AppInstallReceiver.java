package com.kuba.punchbattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Kuba on 2017-03-23.
 * <p>
 * Pluginy powinny mieć pakiety w formacie com.kuba.punchbattery.plugin.NAZWA_PLUGINU
 */

public class AppInstallReceiver extends BroadcastReceiver {
    private static String packagePrefix = "com.kuba.punchbattery.plugin.";

    @Override
    public void onReceive(Context con, Intent i) {
        String packageName = i.getDataString();
        if (packageName.startsWith(packagePrefix))
        // apka to plugin, instalujemy
        {
            Config.plugins.add(packageName);
            Config.save(con);
            Plugin.install(con, packageName);
        }
    }
}
