package com.kuba.punchbattery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class MyCustomAdapter extends ArrayAdapter {

    private final Integer[] imgid;
    private final Activity context;


    public MyCustomAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.list_layout, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.imgid=imgid;


    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_layout, null, true);


        ImageView imageView = (ImageView) rowView.findViewById(R.id.obrazeczki);
        Button button = (Button) rowView.findViewById(R.id.guziczki);


        imageView.setImageResource(imgid[position]);
        button.setText("OK");


        return rowView;
    }
}
