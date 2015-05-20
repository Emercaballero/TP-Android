package com.teamdc.stephendiniz.autoaway.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sscotti on 5/19/15.
 */
public class Utils {


    public static String hyphenatePhoneNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        if (phoneNumber.length() == 10)
            return phoneNumber.substring(0,3) + "-" + phoneNumber.substring(3,6) + "-" + phoneNumber.substring(6,10);

        if (phoneNumber.length() == 11)
            return phoneNumber.substring(0,1) + "-" + phoneNumber.substring(1,4) + "-" + phoneNumber.substring(4,7) + "-" + phoneNumber.substring(7,11);

        //Not 10 digits long - Unable to hyphenate
        return phoneNumber;
    }

    public static String dehyphenate(String number) {
        if (number.length() == 12)
            return number.substring(0,3) + number.substring(4,7) + number.substring(8,12);

        if (number.length() == 14)
            return number.substring(0,1) + number.substring(2,5) + number.substring(6,9) + number.substring(10,14);
        return number;
    }


    /**
     * In your face type erasure
     *
     * @param listables
     * @return
     */
    public static List<Listable> asListable(List<? extends Listable> listables){
        List<Listable> listableList = new ArrayList<Listable>(listables.size());

        listableList.addAll(listables);

        return listableList;
    }

}
