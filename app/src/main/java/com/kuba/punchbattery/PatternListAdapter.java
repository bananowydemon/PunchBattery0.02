package com.kuba.punchbattery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PatternListAdapter extends ArrayAdapter {
    Context parentContext;
    //ArrayList<Pattern> patterns;

    public PatternListAdapter(Activity context, ArrayList<Pattern> pat) {
        super(context, 0, pat);

        parentContext = context;
        //patterns = pat;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pattern pattern = (Pattern) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        // Lookup view for data population
        ImageView image = (ImageView) convertView.findViewById(R.id.obrazeczki);
        Button button = (Button) convertView.findViewById(R.id.guziczki);
        image.setBackground(pattern.createAnimation(parentContext, 1200)); // potem w onclicklistenerze odpalić animacje (image.getBackground().start())
        button.setText("OK");
        return convertView;
    }
}
