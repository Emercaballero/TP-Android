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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.teamdc.stephendiniz.autoaway.classes.GPS;
import com.teamdc.stephendiniz.autoaway.classes.Preferences;

import java.io.IOException;
import java.util.Calendar;


public class Activity_Location extends Activity implements View.OnClickListener {

    private CheckBox checkBox;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBoxNet;
    private Button saveButton;
    private Button cancelButton;
    private Button viewButton;
    private RadioButton radioGps;
    private RadioButton radioNetwork;
    private RadioButton radioNoLocation;
    RadioGroup locationRadioGroup;
    private TextView text;
    private Calendar cal;
    private String lat;
    private String alt;
    private String acc;

    private String selectedProvider;
    private Preferences preferences;

    SharedPreferences prefs;
    final String THEME_PREF		= "themePreference";

    private GPS gps;
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

        gps = GPS.getInstance(this);
        preferences = Preferences.getInstance(this);

        selectedProvider = preferences.getSelectedProvider();

        setContentView(R.layout.location);
        saveButton = (Button) findViewById(R.id.button1);
        cancelButton = (Button) findViewById(R.id.button2);
        viewButton = (Button) findViewById(R.id.button3);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);

        checkBox = (CheckBox) findViewById(R.id.locationCheck);
        checkBox.setOnClickListener(this);
        checkBox.setChecked(gps.isGPSEnabled());

        checkBoxNet = (CheckBox) findViewById(R.id.netCheck);
        checkBoxNet.setOnClickListener(this);
        checkBoxNet.setChecked(gps.isNetworkEnabled());

        checkBox3 = (CheckBox) findViewById(R.id.ciudadCheck);
        checkBox3.setOnClickListener(this);
        checkBox3.setChecked(preferences.getCiudadActivated());

        locationRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGps = (RadioButton) findViewById(R.id.radioGps);
        radioNetwork = (RadioButton) findViewById(R.id.radioNetwork);
        radioNoLocation = (RadioButton) findViewById(R.id.radioNoLocation);

        radioGps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedProvider = LocationManager.GPS_PROVIDER;
                if (gps.isGPSEnabled()){
                    saveButton.setEnabled(true);
                    viewButton.setEnabled(true);
                }
                else{
                    saveButton.setEnabled(false);
                    viewButton.setEnabled(false);
                }
            }
        });

        radioNetwork.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedProvider = LocationManager.NETWORK_PROVIDER;
                if (gps.isNetworkEnabled()){
                    saveButton.setEnabled(true);
                    viewButton.setEnabled(true);
                }
                else{
                    saveButton.setEnabled(false);
                    viewButton.setEnabled(false);
                }
            }
        });

        radioNoLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedProvider = null;
                viewButton.setEnabled(false);
            }
        });

        Integer selectedProviderId;

        if(LocationManager.NETWORK_PROVIDER.equals(selectedProvider)){
            selectedProviderId = radioNetwork.getId();
            if (gps.isNetworkEnabled()){
                saveButton.setEnabled(true);
                viewButton.setEnabled(true);
            }
            else{
                saveButton.setEnabled(false);
                viewButton.setEnabled(false);
            }
        }
        else if(LocationManager.GPS_PROVIDER.equals(selectedProvider)){
            selectedProviderId = radioGps.getId();
            if (gps.isGPSEnabled()){
                saveButton.setEnabled(true);
                viewButton.setEnabled(true);
            }
            else{
                saveButton.setEnabled(false);
                viewButton.setEnabled(false);
            }
        }
        else{
            selectedProviderId = radioNoLocation.getId();
            saveButton.setEnabled(false);
            viewButton.setEnabled(false);
        }

        locationRadioGroup.check(selectedProviderId);

        text = (TextView) findViewById(R.id.textView1);

        checkBox2 = (CheckBox) findViewById(R.id.flashCheck);
        checkBox2.setOnClickListener(this);
        checkBox2.setChecked(preferences.isFlashActivated());
    }

    public void onResume()
    {
        super.onResume();
        checkBox.setChecked(gps.isGPSEnabled());
        checkBoxNet.setChecked(gps.isNetworkEnabled());
    }

    private void setButtonState(boolean estado) {
        checkBox.setChecked(estado);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity__location, menu);

        return true;
    }

    public void onClick(View v) {
        if (v == checkBox || v == checkBoxNet)
            clickCheckBox();
        else if (v == checkBox2)
            clickCheckBox2();
        else if (v == checkBox3)
            clickCheckBox3();
        else if (v == saveButton)
            clickSaveButton();
        else if (v == cancelButton)
            clickCancelButton();
        else if (v == viewButton)
            try {
                clickViewButton();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    private void clickCheckBox() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void clickCheckBox2() {
        if (preferences.isFlashActivated())
            preferences.setFlashActivated(false);
        else
            preferences.setFlashActivated(true);
        checkBox2.setChecked(preferences.isFlashActivated());
    }

    private void clickCheckBox3() {
        if (preferences.getCiudadActivated())
            preferences.setCiudadActivated(false);
        else
            preferences.setCiudadActivated(true);
        checkBox3.setChecked(preferences.getCiudadActivated());
    }

    private void clickSaveButton() {
        preferences.setSelectedProvider(selectedProvider);

        TextView textView = (TextView) findViewById(R.id.textView1);

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_LONG).show();

        finish();
    }

    private void clickCancelButton() {
        finish();
    }

    private void clickViewButton() throws IOException {
        TextView textView = (TextView) findViewById(R.id.textView1);

        String text = "";

        Location location = selectedProvider == null ? null : gps.getCurrentLocation(selectedProvider);

        if (location == null) {
            if (radioGps.isChecked()) {
                text = gps.isGPSEnabled() ? "No GPS signal!" : "Turn GPS on!";
            } else if (radioNetwork.isChecked()) {
                text = "No network signal!";
            }
        } else {
            if (preferences.getCiudadActivated()){
                String ciudad = gps.getCiudad();
                if (ciudad == null)
                    ciudad = "Sin Ciudad";
                text = String.format("%s",ciudad);
            }
            else {
                text = String.format("Latitude=%.3f Longitude=%.3f",location.getLatitude(), location.getLongitude());
            }
        }

        textView.setText(text);
    }
}