package com.mememome.mememome.convertions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dangal on 5/16/15.
 */
public class StringConvertions {

    public static final String DBX_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

    public static long stringDateToMillis(String givenDateString) {

        Locale loc = DateFormat.getAvailableLocales()[0];

        SimpleDateFormat sdf = new SimpleDateFormat(DBX_FORMAT,loc);
        long timeInMilliseconds = System.currentTimeMillis();
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    public static String millisToStringDate(long timestampInMilliSeconds) {

        Locale loc = DateFormat.getAvailableLocales()[0];

        SimpleDateFormat sdf = new SimpleDateFormat(DBX_FORMAT,loc);

        Date date = new Date();
        date.setTime(timestampInMilliSeconds);
        String formattedDate = sdf.format(date);
        return formattedDate;

    }
}
