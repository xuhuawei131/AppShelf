package com.x91tec.appshelf.converter;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class is used for conversion between Date and String;
 *
 * @author oeager
 */
public final class DateFormatter {

    private DateFormatter() {
        //no instance
    }

    private final static DateFormat LOCAL_DATE_FORMAT = DateFormat.getDateInstance();

    private final static DateFormat LOCAL_DATE_TIME_FORMAT = DateFormat.getDateTimeInstance();

    private final static ThreadLocal<DateFormat> SIMPLE_DATE_FORMAT = new ThreadLocal<DateFormat>();

    public static String formatDateTime(long millions) {
        return LOCAL_DATE_TIME_FORMAT.format(new Date(millions));
    }

    public static String formatDate(long millions) {
        return LOCAL_DATE_FORMAT.format(new Date(millions));
    }

    public static String formatDate(long millions, String format) {
        Date date = new Date(millions);
        return formatDate(date, format);
    }

    public static void invalidateThreadLocal() {
        SIMPLE_DATE_FORMAT.set(null);
    }

    public static void initFormatThreadLocal(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        SIMPLE_DATE_FORMAT.set(dateFormat);
    }

    public static String formatDateFrequent(long millions, String format) {
        Date date = new Date(millions);
        if (SIMPLE_DATE_FORMAT.get() == null) {
            initFormatThreadLocal(format);
        }
        return SIMPLE_DATE_FORMAT.get().format(date);
    }

    public static String formatDateTime(Date date) {
        return LOCAL_DATE_TIME_FORMAT.format(date);
    }

    public static String formatDate(Date date, String format) {
        if (TextUtils.isEmpty(format)) {
            return LOCAL_DATE_FORMAT.format(date);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }


    public static Date parseDate(String date, String format) {
        DateFormat dateFormat;
        if (TextUtils.isEmpty(format)) {
            dateFormat = SIMPLE_DATE_FORMAT.get();
            if (dateFormat == null) {
                dateFormat = LOCAL_DATE_FORMAT;
            }
        } else {
            dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        }
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static DateFormat takeSimpleFormat() {
        return SIMPLE_DATE_FORMAT.get();
    }

    public static boolean isDateToday(String mDate) {
        return equalDate(parseDate(mDate, null), new Date());
    }

    public static boolean isDateToday(Date date) {
        Date today = new Date();
        return equalDate(date, today);
    }

    public static boolean equalDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return LOCAL_DATE_FORMAT.format(date1).equals(LOCAL_DATE_FORMAT.format(date2));
    }

    public static int calcOffsetDays(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int y1 = calendar1.get(Calendar.YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
        int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
        int maxDays = 0;
        int day = 0;
        if (y1 - y2 > 0) {
            maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 + maxDays;
        } else if (y1 - y2 < 0) {
            maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 - maxDays;
        } else {
            day = d1 - d2;
        }
        return day;
    }

    /**
     * calculate the number of hours between two difference dates;
     */
    public static int calcOffsetHours(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int h = 0;
        int day = calcOffsetDays(date1, date2);
        h = h1 - h2 + day * 24;
        return h;
    }

    /**
     * the offset minutes between two dates
     */
    public static int calcOffsetMinutes(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int m1 = calendar1.get(Calendar.MINUTE);
        int m2 = calendar2.get(Calendar.MINUTE);
        int h = calcOffsetHours(date1, date2);
        int m = 0;
        m = m1 - m2 + h * 60;
        return m;
    }

    /**
     * to determine a year is Leap or not
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);

    }

    public static boolean isEndOfToday(Date date) {
        if (date == null) {
            return false;
        }
        Date today = new Date();
        return today.before(date);

    }


}
