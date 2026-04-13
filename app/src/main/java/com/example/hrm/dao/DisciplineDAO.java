package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Discipline;

import java.util.ArrayList;
import java.util.List;

public class DisciplineDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public DisciplineDAO(Context context) {
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

    public long insertKyLuat(Discipline discipline) {
        open();
        ContentValues values = new ContentValues();
        values.put("id_nv", discipline.getIdNhanVien());
        values.put("ngay_quyet_dinh", discipline.getNgayQuyetDinh());
        values.put("hinh_thuc", discipline.getHinhThuc());
        values.put("so_tien_phat", discipline.getSoTienPhat());
        values.put("ly_do", discipline.getLyDo());

        long result = database.insert("KyLuat", null, values);
        close();
        return result;
    }

    public int updateKyLuat(Discipline discipline) {
        open();
        ContentValues values = new ContentValues();
        values.put("id_nv", discipline.getIdNhanVien());
        values.put("ngay_quyet_dinh", discipline.getNgayQuyetDinh());
        values.put("hinh_thuc", discipline.getHinhThuc());
        values.put("so_tien_phat", discipline.getSoTienPhat());
        values.put("ly_do", discipline.getLyDo());

        int result = database.update(
                "KyLuat",
                values,
                "id_ky_luat = ?",
                new String[]{String.valueOf(discipline.getIdKyLuat())}
        );
        close();
        return result;
    }

    public int deleteKyLuat(int idKyLuat) {
        open();
        int result = database.delete(
                "KyLuat",
                "id_ky_luat = ?",
                new String[]{String.valueOf(idKyLuat)}
        );
        close();
        return result;
    }

    public List<Discipline> getAllKyLuat() {
        List<Discipline> list = new ArrayList<>();
        open();

        String query = "SELECT kl.id_ky_luat, kl.id_nv, kl.ngay_quyet_dinh, kl.hinh_thuc, " +
                "kl.so_tien_phat, kl.ly_do, nv.ho_ten, nv.ma_nv " +
                "FROM KyLuat kl " +
                "INNER JOIN NhanVien nv ON kl.id_nv = nv.id_nv " +
                "ORDER BY kl.id_ky_luat DESC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Discipline discipline = new Discipline();
                discipline.setIdKyLuat(cursor.getInt(cursor.getColumnIndexOrThrow("id_ky_luat")));
                discipline.setIdNhanVien(cursor.getInt(cursor.getColumnIndexOrThrow("id_nv")));
                discipline.setNgayQuyetDinh(cursor.getString(cursor.getColumnIndexOrThrow("ngay_quyet_dinh")));
                discipline.setHinhThuc(cursor.getString(cursor.getColumnIndexOrThrow("hinh_thuc")));
                discipline.setSoTienPhat(cursor.getDouble(cursor.getColumnIndexOrThrow("so_tien_phat")));
                discipline.setLyDo(cursor.getString(cursor.getColumnIndexOrThrow("ly_do")));
                discipline.setTenNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ho_ten")));
                discipline.setMaNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ma_nv")));

                list.add(discipline);
            } while (cursor.moveToNext());

            cursor.close();
        }

        close();
        return list;
    }

    public List<Discipline> searchKyLuat(String keyword) {
        List<Discipline> list = new ArrayList<>();
        open();

        String query = "SELECT kl.id_ky_luat, kl.id_nv, kl.ngay_quyet_dinh, kl.hinh_thuc, " +
                "kl.so_tien_phat, kl.ly_do, nv.ho_ten, nv.ma_nv " +
                "FROM KyLuat kl " +
                "INNER JOIN NhanVien nv ON kl.id_nv = nv.id_nv " +
                "WHERE nv.ho_ten LIKE ? OR nv.ma_nv LIKE ? " +
                "ORDER BY kl.id_ky_luat DESC";

        String searchKey = "%" + keyword + "%";
        Cursor cursor = database.rawQuery(query, new String[]{searchKey, searchKey});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Discipline discipline = new Discipline();
                discipline.setIdKyLuat(cursor.getInt(cursor.getColumnIndexOrThrow("id_ky_luat")));
                discipline.setIdNhanVien(cursor.getInt(cursor.getColumnIndexOrThrow("id_nv")));
                discipline.setNgayQuyetDinh(cursor.getString(cursor.getColumnIndexOrThrow("ngay_quyet_dinh")));
                discipline.setHinhThuc(cursor.getString(cursor.getColumnIndexOrThrow("hinh_thuc")));
                discipline.setSoTienPhat(cursor.getDouble(cursor.getColumnIndexOrThrow("so_tien_phat")));
                discipline.setLyDo(cursor.getString(cursor.getColumnIndexOrThrow("ly_do")));
                discipline.setTenNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ho_ten")));
                discipline.setMaNhanVien(cursor.getString(cursor.getColumnIndexOrThrow("ma_nv")));

                list.add(discipline);
            } while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return list;
    }
}