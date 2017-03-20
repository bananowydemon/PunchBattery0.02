package com.kuba.punchbattery;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import java.util.Random;

public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int GRAPH_TYPE_LEVEL = 0;
    public static final int GRAPH_TYPE_TEMPERATURE = 1;
    public static final int GRAPH_TYPE_VOLTAGE = 2;
    public static final int GRAPH_TYPE_SYSTEMSETTINGS = 3;

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
        timeSeriesData = LogFile.timeSeriesData(LogFile.read(this, Config.batteryLogFile), noOfPoints); //wyciaga dane z pliku
        List<Double> xAxisSeries = dateToDouble(timeSeriesData.get(0)); // daty

        // trzy serie (poziom baterii, temperatura, napięcie)
        LineGraphSeries seriesLevel = new LineGraphSeries(), seriesTemperature = new LineGraphSeries(), seriesVoltage = new LineGraphSeries();
        seriesLevel.setColor(Color.GREEN);
        seriesTemperature.setColor(Color.RED);
        seriesVoltage.setColor(Color.BLUE);

        int pointNumber = xAxisSeries.size(); // liczba punktow
        BatteryData data;
        for (int i = 0; i < pointNumber; i++) { // dodaje dane z pliku do serii
            data = BatteryData.fromString(timeSeriesData.get(1).get(i));
            if(currentGraphType == GRAPH_TYPE_LEVEL)
                seriesLevel.appendData(new DataPoint(xAxisSeries.get(i), data.level), true, noOfPoints);
            if(currentGraphType == GRAPH_TYPE_TEMPERATURE)
                seriesTemperature.appendData(new DataPoint(xAxisSeries.get(i), data.temperature), true, noOfPoints);
            if(currentGraphType == GRAPH_TYPE_VOLTAGE)
                seriesVoltage.appendData(new DataPoint(xAxisSeries.get(i), data.voltage), true, noOfPoints);
        }

        graphView.removeAllSeries();

        //// ustawiamy formatowanie dla etykiet
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm"); // wyświetla daty jako godziny
        final Calendar calendar = Calendar.getInstance();
        Viewport viewport = graphView.getViewport();
        if(currentGraphType == GRAPH_TYPE_LEVEL) {
            graphView.addSeries(seriesLevel);
            viewport.setYAxisBoundsManual(true);
            viewport.setMinY(0.0D);
            viewport.setMaxY(100.0D);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() { // customowy format etykiet
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        calendar.setTimeInMillis((long) value);
                        return dateFormat.format(calendar.getTimeInMillis());
                    } else
                        return String.format(Locale.ENGLISH, "%d%%", (int) value); // bez miejsc po przecinku -> np 50%
                }
            });
        }
        if(currentGraphType == GRAPH_TYPE_TEMPERATURE) {
            graphView.addSeries(seriesTemperature);
            viewport.setYAxisBoundsManual(false);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        calendar.setTimeInMillis((long) value);
                        return dateFormat.format(calendar.getTimeInMillis());
                    } else
                        return String.format(Locale.ENGLISH, "%.1f℃", value); // z jednym miejscem po przecinku -> np 52.4℃
                }
            });
        }
        if(currentGraphType == GRAPH_TYPE_VOLTAGE) {
            graphView.addSeries(seriesVoltage);
            viewport.setYAxisBoundsManual(false);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        calendar.setTimeInMillis((long) value);
                        return dateFormat.format(calendar.getTimeInMillis());
                    } else
                        return String.format(Locale.ENGLISH, "%.2fV", value / 1000); // z dwoma miejscami po przecinku, zamiana mV na V -> np 4.67V
                }
            });
        }

        //// ustawienie osi X
        Pair<Double, Double> minmax = arrayMinMax(xAxisSeries); // najmniejsza i największa wartość w czasie (x-ach)
        graphView.getGridLabelRenderer().setHumanRounding(false);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // max 3 etykiety godzin
        viewport.setScalable(true);
        viewport.setScrollable(true);
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
    /*private List<Double> dateToDouble(List<String> dates) {
        List<Date> sdfList = new ArrayList<>(); // tu trzymamy czasy jako Date
        List<Double> output = new ArrayList<>();

        // być może double z małej

        int pointNumber = dates.size();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy,HH:mm:ss:SSS");
        for (int i = 0; i < pointNumber; i++) { // petla przerabia daty ze String na Date
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
    }*/

    // nowa wersja, daty jako milisekundy
    private List<Double> dateToDouble(List<String> dates) {
        List<Double> output = new ArrayList<>();

        try {
            int pointNumber = dates.size();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy,HH:mm:ss:SSS");
            sdf.setLenient(false);
            for (int i = 0; i < pointNumber; i++) { // petla przerabia daty ze String na double
                   Date dt = sdf.parse(dates.get(i));
                   output.add((double) dt.getTime()); // dodaje date do listy dat
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return output;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
            if(pos == GRAPH_TYPE_SYSTEMSETTINGS)
                startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
            else
            {
                currentGraphType = pos;
                setupGraph();
            }
    }

    public void onNothingSelected(AdapterView<?> parent) { }


    private Pair<Double, Double> arrayMinMax(List<Double> a)
    {
        double max = a.get(0), min = a.get(0);
        for(Double d : a) {
            if (d > max)
                max = d;
            if (d < min)
                min = d;
        }
        return Pair.create(min, max);
    }
}
