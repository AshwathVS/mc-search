package org.mcsearch.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateUtils {
    public static Date parseDate(String dateString, DateFormat dateFormat) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException ex) {
            return new Date(Long.MIN_VALUE);
        }
    }

    public static long getCurrentTime() {
        return new Date().getTime();
    }

    public static long getTimeDiffFromNow(long start) {
        return new Date().getTime() - start;
    }
}