package com.kuba.punchbattery;

/**
 * Created by Kuba on 05.01.2017.
 */

import java.util.List;
import java.util.Set;

interface SweepListener {

    List<ProcessHolder> getRunningApps();

    void onSweepStart();

    void onSweepFinish(Set<String> packages);

}
