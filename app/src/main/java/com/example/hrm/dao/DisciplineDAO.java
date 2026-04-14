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

    public DisciplineDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertKyLuat(Discipline d) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_ID_NV, d.getIdNhanVien());
        v.put(DBHelper.COL_NGAY_QUYET_DINH, d.getNgayQuyetDinh());
        v.put(DBHelper.COL_HINH_THUC, d.getHinhThuc());
        v.put(DBHelper.COL_SO_TIEN_PHAT, d.getSoTienPhat());
        v.put(DBHelper.COL_LY_DO, d.getLyDo());
        long res = db.insert(DBHelper.TABLE_KYLUAT, null, v);
        db.close();
        return res;
    }

    public int updateKyLuat(Discipline d) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_ID_NV, d.getIdNhanVien());
        v.put(DBHelper.COL_NGAY_QUYET_DINH, d.getNgayQuyetDinh());
        v.put(DBHelper.COL_HINH_THUC, d.getHinhThuc());
        v.put(DBHelper.COL_SO_TIEN_PHAT, d.getSoTienPhat());
        v.put(DBHelper.COL_LY_DO, d.getLyDo());
        int res = db.update(DBHelper.TABLE_KYLUAT, v, DBHelper.COL_ID_KYLUAT + " = ?",
                new String[]{String.valueOf(d.getIdKyLuat())});
        db.close();
        return res;
    }

    public int deleteKyLuat(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int res = db.delete(DBHelper.TABLE_KYLUAT, DBHelper.COL_ID_KYLUAT + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return res;
    }

    public List<Discipline> getAllKyLuat() {
        List<Discipline> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT kl.*, nv." + DBHelper.COL_HO_TEN + ", nv." + DBHelper.COL_MA_NV +
                " FROM " + DBHelper.TABLE_KYLUAT + " kl " +
                " INNER JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON kl." + DBHelper.COL_ID_NV + " = nv." + DBHelper.COL_ID_NV +
                " ORDER BY kl." + DBHelper.COL_ID_KYLUAT + " DESC";
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            do {
                Discipline d = new Discipline();
                d.setIdKyLuat(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_KYLUAT)));
                d.setIdNhanVien(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                d.setNgayQuyetDinh(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NGAY_QUYET_DINH)));
                d.setHinhThuc(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HINH_THUC)));
                d.setSoTienPhat(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_SO_TIEN_PHAT)));
                d.setLyDo(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_LY_DO)));
                d.setTenNhanVien(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                d.setMaNhanVien(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                list.add(d);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return list;
    }
}