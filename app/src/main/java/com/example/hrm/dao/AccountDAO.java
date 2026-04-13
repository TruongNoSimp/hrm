package com.example.hrm.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;

public class AccountDAO {

    private DBHelper dbHelper;

    public AccountDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + DBHelper.TAI_KHOAN +
                            " WHERE " + DBHelper.COL_USERNAME + " = ? AND " +
                            DBHelper.COL_PASSWORD + " = ?",
                    new String[]{username, password}
            );

            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
}