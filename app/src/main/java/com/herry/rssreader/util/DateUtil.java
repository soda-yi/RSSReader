package com.herry.rssreader.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private DateUtil() {}

    public static CharSequence formatDate(Long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date beforeYesterday = calendar.getTime();
        if (isSameDay(date, today)) {
            return "今天";
        } else if (isSameDay(date, yesterday)) {
            return "昨天";
        } else if (isSameDay(date, beforeYesterday)) {
            return "前天";
        } else {
            return DateFormat.format("yyyy年MM月dd日", date);
        }
    }

    public static CharSequence formatTime(Long time) {
        return new SimpleDateFormat("HH:mm").format(new Date(time));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameYear(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}
