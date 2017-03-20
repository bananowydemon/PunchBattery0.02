package com.kuba.punchbattery;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class PatternsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patterns);

        PatternListAdapter adapter = new PatternListAdapter(this, Global.patterns);

        ListView listView = (ListView) findViewById(R.id.pattern_list);
        listView.setAdapter(adapter);

    }

}
