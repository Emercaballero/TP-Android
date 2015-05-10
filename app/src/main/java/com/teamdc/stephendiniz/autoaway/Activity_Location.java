package com.teamdc.stephendiniz.autoaway;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Calendar;


public class Activity_Location extends Activity implements View.OnClickListener {

    private LocationManager locationManager;
    private CheckBox checkBox;
    private Button button1;
    private Button button2;
    private Button button3;
    private RadioButton radio0;
    private RadioButton radio1;
    private RadioButton radio2;
    private TextView text;
    private Calendar cal;
    private String lat;
    private String alt;
    private String acc;

    SharedPreferences prefs;
    final String THEME_PREF		= "themePreference";

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            if(prefs.getString(THEME_PREF, "LIGHT").equals("LIGHT"))
                setTheme(R.style.HoloLight);
            else
                setTheme(R.style.HoloDark);
        }

        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);

        setContentView(R.layout.location);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        checkBox.setOnClickListener(this);
        radio0 = (RadioButton) findViewById(R.id.radio0);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio0.setOnClickListener(this);
        radio1.setOnClickListener(this);
        radio2.setOnClickListener(this);
        text = (TextView) findViewById(R.id.textView1);
    }

    private void setButtonState() {
        boolean isGps = locationManager.isProviderEnabled("gps");
        checkBox.setChecked(isGps);
        button1.setEnabled(isGps | radio1.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity__location, menu);

        return true;
    }

    public void onClick(View v) {
        if (v == checkBox)
            clickCheckBox();
        else if (v == button1)
            clickButton1();
        else if (v == button2)
            clickButton2();
        else if (v == button3)
            clickButton3();
        else
            setButtonState(); // for radio buttons
    }

    private void clickCheckBox() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        checkBox.setChecked(locationManager.isProviderEnabled("gps"));
    }

    private void clickButton1() { //Boton Save
        TextView textView;
        int minIni, minFin;
        textView = (TextView) findViewById(R.id.textView1);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        cal = Calendar.getInstance();
        Location location;
        if(radio0.isChecked()) {
            minIni = cal.get(Calendar.MINUTE);
            do {
                location = locationManager.getLastKnownLocation("gps");
                minFin = cal.get(Calendar.MINUTE);
            } while (location == null || minFin != minIni);
        }
        else if (radio1.isChecked())
            location = locationManager.getLastKnownLocation("network");
        else
            location = null;
        if (location == null) {
            if (radio0.isChecked()) { //aca es localizacion GPS
                if (locationManager.isProviderEnabled("gps"))
                    textView.setText("No GPS signal!");
                else
                    textView.setText("Turn GPS on!");
            } else
                textView.setText("Network not enabled!");
        } else if (radio1.isChecked()){ //aca es localizacion Network
            long now = Calendar.getInstance().getTimeInMillis();
            textView.setText(String.format("Latitude = %s\nLongitude = %s\n"
                            + "Accuracy = %f\n" + "%d seconds ago",
                    location.getLatitude(), location.getLongitude(),
                    location.getAccuracy(), (now - location.getTime()) / 1000));
        }
        else {
            //Aca es si ponemos sin localizacion.
        }
    }

    private void clickButton2() { //Boton Cancel
        TextView textView;
        int minIni, minFin;
        textView = (TextView) findViewById(R.id.textView1);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        cal = Calendar.getInstance();
        Location location;
        if (radio0.isChecked()) {
            minIni = cal.get(Calendar.MINUTE);
            do {
                location = locationManager.getLastKnownLocation("gps");
                minFin = cal.get(Calendar.MINUTE);
            } while (location == null || minFin != minIni);
        }
        else if (radio1.isChecked())
            location = locationManager.getLastKnownLocation("network");
        else
            location = null;
        if (location == null) {
            if (radio0.isChecked()) {
                if (locationManager.isProviderEnabled("gps"))
                    textView.setText("No GPS signal!");
                else
                    textView.setText("Turn GPS on!");
            } else
                textView.setText("Network not enabled!");
        } else {
            long now = Calendar.getInstance().getTimeInMillis();
            textView.setText(String.format("Latitude = %s\nLongitude = %s\n"
                            + "Accuracy = %f\n" + "%d seconds ago",
                    location.getLatitude(), location.getLongitude(),
                    location.getAccuracy(), (now - location.getTime()) / 1000));
        }
    }

    private void clickButton3() { //Boton View
        TextView textView;
        int minIni, minFin;
        textView = (TextView) findViewById(R.id.textView1);

        Location location;
        if (radio0.isChecked()) {
            location = locationManager.getLastKnownLocation("gps");
        }
        else if (radio1.isChecked())
            location = locationManager.getLastKnownLocation("network");
        else
            location = null;
        if (location == null) {
            if (radio0.isChecked()) {
                if (locationManager.isProviderEnabled("gps"))
                    textView.setText("No GPS signal!");
                else
                    textView.setText("Turn GPS on!");
            } else if (radio1.isChecked()) {
                textView.setText("Network not enabled!");
            }
            else
                textView.setText("");
        } else {
            textView.setText(String.format("Latitude = %.1f\nLongitude = %.1f\n",
                    location.getLatitude(), location.getAltitude()));
        }
    }
}
