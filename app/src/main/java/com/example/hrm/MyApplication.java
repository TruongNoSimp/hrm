package com.example.hrm;
import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Đọc trạng thái Dark Mode ngay khi app vừa khởi chạy
        SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // Ra lệnh cho toàn app dùng màu gì
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}