package com.teamdc.stephendiniz.autoaway.classes;

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

}
