package com.kuba.punchbattery;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PatternsActivity extends ListActivity {

    Integer[] imgid={
            R.drawable.v3,
            R.drawable.batt,

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patterns);

        this.setListAdapter(new ArrayAdapter(this, R.layout.list_layout));




    }

}
