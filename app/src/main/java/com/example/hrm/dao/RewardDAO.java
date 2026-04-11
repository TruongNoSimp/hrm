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
    private SQLiteDatabase database;

    public RewardDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public long insertKhenThuong(Reward reward) {
        open();
        ContentValues values = new ContentValues();
        values.put("id_nv", reward.getIdNhanVien());
        values.put("ngay_quyet_dinh", reward.getNgayQuyetDinh());
        values.put("hinh_thuc", reward.getHinhThuc());
        values.put("so_tien_thuong", reward.getSoTienThuong());
        values.put("ly_do", reward.getLyDo());

        long result = database.insert("KhenThuong", null, values);
        close();
        return result;
    }

    public int updateKhenThuong(Reward reward) {
        open();
        ContentValues values = new ContentValues();
        values.put("id_nv", reward.getIdNhanVien());
        values.put("ngay_quyet_dinh", reward.getNgayQuyetDinh());
        values.put("hinh_thuc", reward.getHinhThuc());
        values.put("so_tien_thuong", reward.getSoTienThuong());
        values.put("ly_do", reward.getLyDo());

        int result = database.update(
                "KhenThuong",
                values,
                "id_khen_thuong = ?",
                new String[]{String.valueOf(reward.getIdKhenThuong())}
        );
        close();
        return result;
    }

    public int deleteKhenThuong(int idKhenThuong) {
        open();
        int result = database.delete(
                "KhenThuong",
                "id_khen_thuong = ?",
                new String[]{String.valueOf(idKhenThuong)}
        );
        close();
        return result;
    }

    public List<Reward> getAllKhenThuong() {
        List<Reward> list = new ArrayList<>();
        open();

        String query = "SELECT kt.id_khen_thuong, kt.id_nv, kt.ngay_quyet_dinh, kt.hinh_thuc, " +
                "kt.so_tien_thuong, kt.ly_do, nv.ho_ten, nv.ma_nv " +
                "FROM KhenThuong kt " +
                "INNER JOIN NhanVien nv ON kt.id_nv = nv.id_nv " +
                "ORDER BY kt.id_khen_thuong DESC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Reward kt = new Reward();
                kt.setIdKhenThuong(cursor.getInt(cursor.getColumnIndexOrThrow("id_khen_thuong")));
                kt.setIdNhanVien(cursor.getInt(cursor.getColumnIndexOrThrow("id_nv")));
                kt.setNgayQuyetDinh(cursor.getString(cursor.getColumnIndexOrThrow("ngay_quyet_dinh")));
                kt.setHinhThuc(cursor.getString(cursor.getColumnIndexOrThrow("hinh_thuc")));
                kt.setSoTienThuong(cursor.getDouble(cursor.getColumnIndexOrThrow("so_tien_thuong")));
                kt.setLyDo(cursor.getString(cursor.getColumnIndexOrThrow("ly_do")));
                kt.setTenNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ho_ten")));
                kt.setMaNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ma_nv")));

                list.add(kt);
            } while (cursor.moveToNext());

            cursor.close();
        }

        close();
        return list;
    }
}