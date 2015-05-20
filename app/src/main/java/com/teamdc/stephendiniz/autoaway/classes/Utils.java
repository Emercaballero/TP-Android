package com.teamdc.stephendiniz.autoaway.classes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sscotti on 5/19/15.
 */
public class Utils {

    private final static String TAG = "Utils";

    public static String hyphenatePhoneNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        if (phoneNumber.length() == 10)
            return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);

        if (phoneNumber.length() == 11)
            return phoneNumber.substring(0, 1) + "-" + phoneNumber.substring(1, 4) + "-" + phoneNumber.substring(4, 7) + "-" + phoneNumber.substring(7, 11);

        //Not 10 digits long - Unable to hyphenate
        return phoneNumber;
    }

    public static String dehyphenate(String number) {
        if (number.length() == 12)
            return number.substring(0, 3) + number.substring(4, 7) + number.substring(8, 12);

        if (number.length() == 14)
            return number.substring(0, 1) + number.substring(2, 5) + number.substring(6, 9) + number.substring(10, 14);
        return number;
    }


    /**
     * In your face type erasure
     *
     * @param listables
     * @return
     */
    public static List<Listable> asListable(List<? extends Listable> listables) {
        List<Listable> listableList = new ArrayList<Listable>(listables.size());

        listableList.addAll(listables);

        return listableList;
    }


    public static <T> List<T> readFile(String fileName, InputStream inputStream, RegisterDeserializer<T> converter) throws IOException {
        List<T> list = new ArrayList<T>();

        InputStreamReader iReader = new InputStreamReader(inputStream);
        BufferedReader bReader = new BufferedReader(iReader);

        String line;
        //Should be in groups of TWO!
        while ((line = bReader.readLine()) != null) {
            list.add(converter.deserialize(line));
        }

        inputStream.close();

        Log.i(TAG, list.size() + " register(s) read from file " + fileName);

        return list;
    }

    public static <T> void saveToFile(String fileName, OutputStream outputStream, List<T> list, RegisterSerializer<T> serializer) throws IOException {

        OutputStreamWriter oWriter = new OutputStreamWriter(outputStream);

        try{
            for(T object : list){

                oWriter.append(serializer.serialize(object)).append("\n");

            }
        } finally {
            oWriter.flush();
            oWriter.close();
        }



    }

}