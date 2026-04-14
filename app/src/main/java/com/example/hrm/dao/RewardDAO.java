package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Reward;

import java.util.ArrayList;
import java.util.List;

public class RewardDAO {
    private DBHelper dbHelper;

    public RewardDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertKhenThuong(Reward reward) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_ID_NV, reward.getIdNhanVien());
        values.put(DBHelper.COL_NGAY_QUYET_DINH, reward.getNgayQuyetDinh());
        values.put(DBHelper.COL_HINH_THUC, reward.getHinhThuc());
        values.put(DBHelper.COL_SO_TIEN_THUONG, reward.getSoTienThuong());
        values.put(DBHelper.COL_LY_DO, reward.getLyDo());

        long result = database.insert(DBHelper.TABLE_KHENTHUONG, null, values);
        database.close();
        return result;
    }

    public int updateKhenThuong(Reward reward) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_ID_NV, reward.getIdNhanVien());
        values.put(DBHelper.COL_NGAY_QUYET_DINH, reward.getNgayQuyetDinh());
        values.put(DBHelper.COL_HINH_THUC, reward.getHinhThuc());
        values.put(DBHelper.COL_SO_TIEN_THUONG, reward.getSoTienThuong());
        values.put(DBHelper.COL_LY_DO, reward.getLyDo());

        int result = database.update(
                DBHelper.TABLE_KHENTHUONG,
                values,
                DBHelper.COL_ID_KHENTHUONG + " = ?",
                new String[]{String.valueOf(reward.getIdKhenThuong())}
        );
        database.close();
        return result;
    }

    public int deleteKhenThuong(int idKhenThuong) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int result = database.delete(
                DBHelper.TABLE_KHENTHUONG,
                DBHelper.COL_ID_KHENTHUONG + " = ?",
                new String[]{String.valueOf(idKhenThuong)}
        );
        database.close();
        return result;
    }

    public List<Reward> getAllKhenThuong() {
        List<Reward> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String query = "SELECT kt.*, nv." + DBHelper.COL_HO_TEN + ", nv." + DBHelper.COL_MA_NV +
                " FROM " + DBHelper.TABLE_KHENTHUONG + " kt " +
                " INNER JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON kt." + DBHelper.COL_ID_NV + " = nv." + DBHelper.COL_ID_NV +
                " ORDER BY kt." + DBHelper.COL_ID_KHENTHUONG + " DESC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Reward kt = new Reward();
                kt.setIdKhenThuong(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_KHENTHUONG)));
                kt.setIdNhanVien(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                kt.setNgayQuyetDinh(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NGAY_QUYET_DINH)));
                kt.setHinhThuc(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HINH_THUC)));
                kt.setSoTienThuong(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_SO_TIEN_THUONG)));
                kt.setLyDo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_LY_DO)));
                kt.setTenNhanVien(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                kt.setMaNhanVien(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                list.add(kt);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return list;
    }
}