package com.kuba.punchbattery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import java.util.Random;

public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public final int GRAPH_TYPE_LEVEL = 0;
    public final int GRAPH_TYPE_TEMPERATURE = 1;

    private LineGraphSeries series;
    //private static final Random RANDOM = new Random();
    //private int lastX = 0;
    private List<List<String>>  timeSeriesData; // timeSeriesData.get(0) -daty, timeSeriesData.get(1) -watrosci
    private int noOfPoints = 288; // maks liczba punktow w metodzie onCreate
    private int refreshEveryNPoints = 5; // co ile puntow przerysowac wykres
    private int currentGraphType = GRAPH_TYPE_LEVEL;

    private void setupList()
    {
        Spinner list = (Spinner) findViewById(R.id.graph_type_select);

       ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
               R.array.graph_type_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list.setAdapter(adapter);

        list.setOnItemSelectedListener(this);
    }

    private void setupGraph()
    {
        GraphView graphView = (GraphView)findViewById(R.id.graph);
        series = new LineGraphSeries();
        timeSeriesData = LogFile.timeSeriesData(LogFile.read(this, Config.batteryLogFile), noOfPoints); //wyciaga dane z pliku
        List<Double> xAxisSeries = dateToDouble(timeSeriesData.get(0)); // daty

        int pointNumber = xAxisSeries.size(); // liczba punktow
        BatteryData data;
        double y = 0.0D;
        for (int i = 0; i < pointNumber; i++) { // dodaje dane z pliku do serii
            data = BatteryData.fromString(timeSeriesData.get(1).get(i));
            //double currentBalanceDbl = Double.parseDouble(timeSeriesData.get(1).get(i).replaceAll(" ","."));
            if(currentGraphType == GRAPH_TYPE_LEVEL)
                y = data.level;
            else if(currentGraphType == GRAPH_TYPE_TEMPERATURE)
                y = data.temperature;
            series.appendData(new DataPoint(xAxisSeries.get(i), y), true, noOfPoints);

        }

        graphView.removeAllSeries();
        graphView.addSeries(series);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true); // !!!!! wydaje mi sie, ze wtedy bedzie dynamicznie, chociaz moze ponizsze setScalable cos robi, trzeba probowac
        viewport.setMinY(0.0D);
        viewport.setMaxY(100.0D);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0.0D);
        viewport.setMaxX(arrayMax(xAxisSeries));
        viewport.setScalable(true);
    }

    private double arrayMax(List<Double> a)
    {
        double max = a.get(0);
        for(Double d : a)
            if(d > max)
                max = d;
        return max;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupList();
        setupGraph();
    }

    protected void onResume() { // !!!!! zrobic kontrole dlugosci pliku w LogFile, uzyc tutaj
        super.onResume();
        (new Thread(new Runnable() {
            public void run() {
                for(int i = 0; i < 100; ++i) {
                    GraphActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            GraphActivity.this.addEntry();
                        }
                    });

                    try {
                        Thread.sleep(600L);
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    }
                }

            }
        })).start();
    }

    private void addEntry() { // !!!! zrobic kontrole wielkosci obiektu series, np co 5 odczytow .resetData() i wprowadzic od nowa

        //this.series.appendData(new DataPoint((double)(this.lastX++), 22.2D), true, 10); // zamiast 22.2D nowy odczyt

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);

        //sprawdź
        return true;
    }

    // zwraca liste dat przeliczonych na Double, zeby podstawic do wykresu do osi X
    private List<Double> dateToDouble(List<String> dates) {
        List<Date> sdfList = new ArrayList<>(); // tu trzymamy czasy jako Date
        List<Double> output = new ArrayList<>();

        // być może double z małej

        int pointNumber = dates.size();
        for (int i = 0; i < pointNumber; i++) { // petla przerabia daty ze String na Date
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy,HH:mm:ss:SSS");
            sdf.setLenient(false);
            try {
                Date dt = sdf.parse(dates.get(i));
                sdfList.add(dt); // dodaje date do listy dat
            } catch (ParseException ex) {
                ex.printStackTrace();
                // i++;    // jakies pomijanie blednych danych? trzebaby tez uwzglednic przy wartosciach Y
            }
        }

        // daty przerabiamy na double w godzinach, pierwsza dana to 0, ostatnia to liczba godzin od pierwszej
        try {
            Date firstDate = sdfList.get(0);
            for (int i = 0; i < pointNumber; i++) {
                Date secondDate = sdfList.get(i);
                double difference = (double) secondDate.getTime() / 1000 / 3600 - (double) firstDate.getTime() / 1000 / 3600;
                output.add(difference);
            }
        } catch (IndexOutOfBoundsException var69) {
            var69.printStackTrace();
        }
        return output;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        currentGraphType = pos;
        setupGraph();
    }

    public void onNothingSelected(AdapterView<?> parent) { }
}
