package com.analytics.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    private final static String DEFAULT_PATTERN = "yyyy/MM/dd";

    public static String getDateString(Date date) {
        simpleDateFormat.applyPattern(DEFAULT_PATTERN);
        return simpleDateFormat.format(date);
    }

    public static String getDateStringInQuotes(Date date) {
        return "'" + getDateString(date) + "'";
    }

    public static Date getEndRetentionDate(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);
        return calendar.getTime();
    }
}
