package com.example.hrm.dao;

import android.content.ContentValues;
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
                    "SELECT * FROM " + DBHelper.TABLE_TAIKHOAN +
                            " WHERE " + DBHelper.COL_USERNAME + " = ? AND " +
                            DBHelper.COL_PASSWORD + " = ?",
                    new String[]{username, password}
            );

            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean updatePassword(String username, String oldPass, String newPass) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlCheck = "SELECT * FROM " + DBHelper.TABLE_TAIKHOAN +
                " WHERE " + DBHelper.COL_USERNAME + " = ? AND " + DBHelper.COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(sqlCheck, new String[]{username, oldPass});

        if (cursor.getCount() > 0) {
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_PASSWORD, newPass);

            int result = db.update(DBHelper.TABLE_TAIKHOAN, values,
                    DBHelper.COL_USERNAME + " = ?", new String[]{username});
            return result > 0;
        }

        cursor.close();
        return false;
    }

    public String checkLoginAndGetName(String user, String pass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT " + DBHelper.COL_ADMINNAME +
                " FROM " + DBHelper.TABLE_TAIKHOAN +
                " WHERE " + DBHelper.COL_USERNAME + " = ? AND " + DBHelper.COL_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{user, pass});
        String name = null;

        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }
}