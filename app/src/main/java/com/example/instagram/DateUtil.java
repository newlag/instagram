package com.example.instagram;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {


    public static String getDate(Long time) {
        Long difference = (getCurrentTime() - time) / (1000 * 60 * 60 * 24);

        return String.valueOf(difference);
    }

    public static boolean isStoryActive(Long time) {

        if (getCurrentTime() - time < (1000 * 60 * 60 * 24)) return true;
        return false;
    }

    public static long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String hoursAgo(Long time) {
        Long difference = (getCurrentTime() - time) / (1000 * 60 * 60);
        return String.valueOf(difference);
    }

}
