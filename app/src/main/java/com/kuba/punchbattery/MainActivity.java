package com.kuba.punchbattery;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final SensorEventListener TemperatureSensorListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
                String val = event.values[0] + "°C";
                textTEMPERATURE_reading.setText(val);
            }
        }

    };
    TextView batteryLevel_reading, textTEMPERATURE_reading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BatteryData batteryData = BatteryData.getCurrent(this);
        textTEMPERATURE_reading = (TextView)findViewById(R.id.Text2);
        textTEMPERATURE_reading.setText(batteryData.temperature + " *C");
        batteryLevel_reading = (TextView)findViewById(R.id.Text4);
        batteryLevel_reading.setText(batteryData.level + "%");

        // sprawdzamy czy konfiguracja załadowana, jeśli nie to ładujemy i odświeżamy wartości globalne
        if(Config.load(this))
            Global.reloadValues(this);

        ImageView imageView = (ImageView) findViewById(R.id.ImageView1);
        AnimationDrawable batteryAnimation = Global.currentPattern.createAnimation(this, 1000);
        imageView.setBackground(batteryAnimation);
        batteryAnimation.start();

        DataCollector.turnAlarmOnOff(this, true, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_batt) {
            // Handle the camera action
        } else if (id == R.id.nav_graphs) {

        } else if (id == R.id.nav_patterns) {

        } else if (id == R.id.nav_pro) {

        } else if (id == R.id.nav_shop) {

        } else if (id == R.id.nav_task) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void patterns(MenuItem item) {
        Intent intent= new Intent(this, PatternsActivity.class);
        startActivity(intent);
    }

    public void graph (MenuItem item) {
        Intent intent= new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

}
