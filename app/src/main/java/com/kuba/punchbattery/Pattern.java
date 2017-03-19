package com.kuba.punchbattery;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;

/**
 * Created by Kuba on 2017-03-19.
 */

public class Pattern {
    public int id;
    public int[] images;

    public static Pattern get(Context c, int id)
    {
        Pattern pat = new Pattern();
        Resources res = c.getResources();
        TypedArray ta = res.obtainTypedArray(R.array.patterns);
        pat.images = res.getIntArray(ta.getResourceId(id, 0));
        return pat;
    }

    public int chooseImageResource(BatteryData data)
    {
        int step = 100 / images.length;
        int id = data.level / step;
        if(id == images.length) id--; // gdyby czasem się zjebało i przeskoczyło images.length
        return images[id];
    }
}
