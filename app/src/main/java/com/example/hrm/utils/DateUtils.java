package com.example.hrm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm";

    /**
     * Format ngày từ yyyy-MM-dd sang định dạng người dùng chọn
     */
    public static String formatDisplayDate(Context context, String dateSql) {
        SharedPreferences prefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String pattern = prefs.getString("date_format", DEFAULT_DATE_FORMAT);
        return format(dateSql, "yyyy-MM-dd", pattern);
    }

    public static String formatDisplayTime(Context context, String timeSql) {
        SharedPreferences prefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String pattern = prefs.getString("time_format", DEFAULT_TIME_FORMAT);
        return format(timeSql, "HH:mm:ss", pattern);
    }

    // Hàm lõi để thực hiện việc parse và format
    private static String format(String value, String fromPattern, String toPattern) {
        try {
            if (value == null || value.isEmpty()) return "";
            SimpleDateFormat sdfIn = new SimpleDateFormat(fromPattern, Locale.getDefault());
            SimpleDateFormat sdfOut = new SimpleDateFormat(toPattern, Locale.getDefault());
            return sdfOut.format(sdfIn.parse(value));
        } catch (Exception e) {
            return value;
        }
    }
}