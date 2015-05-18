package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sscotti on 5/17/15.
 */
public class Preferences {

    private static final Preferences INSTANCE = new Preferences();

    private Context context;
    private Preferences(){}

    public static final Preferences getInstance(Context context){
        INSTANCE.context = context;
        return INSTANCE;
    }

    public String getSelectedProvider(){
        SharedPreferences preferences = this.context.getSharedPreferences("datosgps",Context.MODE_MULTI_PROCESS);
        return preferences.getString("provider", null);
    }

    public void setSelectedProvider(String provider){
        SharedPreferences preferences = this.context.getSharedPreferences("datosgps",Context.MODE_PRIVATE);
        preferences.edit().putString("provider", provider).commit();
    }

    public boolean isLocationActivated(){
        SharedPreferences preferences = this.context.getSharedPreferences("datosgps",Context.MODE_PRIVATE);
        return preferences.getBoolean("location_activated", Boolean.FALSE);
    }

    public void setLocationActivated(boolean locationActivated){
        SharedPreferences preferences = this.context.getSharedPreferences("datosgps",Context.MODE_PRIVATE);
        preferences.edit().putBoolean("location_activated", locationActivated).commit();
    }

}
