package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.teamdc.stephendiniz.autoaway.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by sscotti on 5/22/15.
 */
public class FullLocation extends Location {

    private String city;

    private String state;

    private String country;

    public FullLocation(Location l, Context context) {
        super(l);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(this.getLatitude(), this.getLongitude(), 1);

            this.city       = addresses.get(0).getAddressLine(0);
            this.state      = addresses.get(0).getAddressLine(1);
            this.country    = addresses.get(0).getAddressLine(2);

        } catch (IOException e) {
            this.city       = context.getString(R.string.unknown);
            this.state      = context.getString(R.string.unknown);
            this.country    = context.getString(R.string.unknown);
        }

    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }
}
