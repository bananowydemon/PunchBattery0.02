package com.kuba.punchbattery;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;

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
        TypedArray t2 = res.obtainTypedArray(ta.getResourceId(id, 0));
        pat.images = new int[t2.length()];
        for(int i = 0; i < t2.length(); i++)
            pat.images[i] = t2.getResourceId(i,0);
        ta.recycle();
        return pat;
    }

    public static ArrayList<Pattern> getAllPatterns(Context c)
    {
        Resources res = c.getResources();
        TypedArray ta = res.obtainTypedArray(R.array.patterns);
        int count = ta.length();
        ArrayList<Pattern> patterns = new ArrayList<>(count);
        for(int i = 0; i < count; i++)
        {
            TypedArray t2 = res.obtainTypedArray(ta.getResourceId(i, 0));
            Pattern pat = new Pattern();
            pat.images = new int[t2.length()];
            for (int j = 0; j < t2.length(); j++)
                pat.images[j] = t2.getResourceId(j, 0);
            patterns.add(pat);
            t2.recycle();
        }
        ta.recycle();
        return patterns;
    }

    public static int getPatternCount(Context c)
    {
        return c.getResources().obtainTypedArray(R.array.patterns).length();
    }

    public int chooseImageResource(BatteryData data)
    {
        int step = 100 / images.length;
        int id = data.level / step;
        if(id == images.length) id--; // gdyby czasem się zjebało i przeskoczyło images.length
        return images[id];
    }

    // tworzy animację patternu składającą się z osobnych imageów
    //do czasu *muahaahahahah*
    public AnimationDrawable createAnimation(Context c, int duration)
    {
        AnimationDrawable anim = new AnimationDrawable();
        for(int id : images)
            anim.addFrame(ContextCompat.getDrawable(c, id), duration);
        anim.setOneShot(false);
        return anim;
    }
}
