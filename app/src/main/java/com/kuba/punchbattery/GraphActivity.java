package com.kuba.punchbattery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import java.util.Random;

public class GraphActivity extends AppCompatActivity {
    private LineGraphSeries series;
    //private static final Random RANDOM = new Random();
    //private int lastX = 0;
    private List<List<String>>  timeSeriesData; // timeSeriesData.get(0) -daty, timeSeriesData.get(1) -watrosci
    private int noOfPoints = 288; // maks liczba punktow w metodzie onCreate
    private int refreshEveryNPoints = 5; // co ile puntow przerysowac wykres

    public GraphActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        //sprawdź
        this.setSupportActionBar(toolbar);
        GraphView graphView = (GraphView)this.findViewById(R.id.graph);


        //spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);





        this.series = new LineGraphSeries();

        this.timeSeriesData = new ArrayList<List<String>>();
        this.timeSeriesData = LogFile.timeSeriesData(LogFile.read("batteryLevel.txt"), this.noOfPoints); //wyciaga dane z pliku
        List<Double> xAxisSeries = new ArrayList<Double>();
        xAxisSeries = this.dateToDouble(timeSeriesData.get(0)); // zbior rzednych punktow

        int pointNumber = xAxisSeries.size(); // liczba punktow
        for (int i = 0; i < pointNumber; i++) { // dodaje dane z pliku do serii

            double currentBalanceDbl = Double.parseDouble(timeSeriesData.get(1).get(i).replaceAll(" ","."));


            this.series.appendData(new DataPoint(xAxisSeries.get(i), currentBalanceDbl), true, this.noOfPoints);

        }

        graphView.addSeries(this.series);
        Viewport viewport = graphView.getViewport();
        //viewport.setYAxisBoundsManual(true); // !!!!! wydaje mi sie, ze wtedy bedzie dynamicznie, chociaz moze ponizsze setScalable cos robi, trzeba probowac
        //viewport.setMinY(0.0D);
        //viewport.setMaxY(100.0D);
        viewport.setScalable(true);


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
        List<Date> sdfList = new ArrayList<Date>(); // tu trzymamy czasy jako Date
        List<Double> output = new ArrayList<Double>();

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


        List<List<Double>> output = new ArrayList<List<Double>>();
}
