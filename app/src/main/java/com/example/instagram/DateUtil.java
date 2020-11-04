package com.example.instagram;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static String getCurrentTime() {
        return ServerValue.TIMESTAMP.get("timestamp");
    }

    public static String getDate(Long time) {
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        Long difference = (currentTime - time) / (1000 * 60 * 60 * 24);

        return String.valueOf(difference);
    }

}
