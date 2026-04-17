package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.dto.TrainingDTO;

import java.util.ArrayList;
import java.util.List;

public class TrainingDAO {
    private DBHelper dbHelper;

    public TrainingDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<TrainingDTO> getAllTrainingInfo() {
        List<TrainingDTO> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT kh." + DBHelper.COL_ID_KH + ", kh." + DBHelper.COL_TEN_KH + ", " +
                "kh." + DBHelper.COL_GIANG_VIEN + ", kh." + DBHelper.COL_NGAY_BD + ", " +
                "kh." + DBHelper.COL_NGAY_KT + ", nv." + DBHelper.COL_HO_TEN + ", " +
                "nv." + DBHelper.COL_ID_NV + ", dt." + DBHelper.COL_KET_QUA +
                " FROM " + DBHelper.TABLE_KHOAHOC + " kh " +
                "JOIN " + DBHelper.TABLE_CHITIET_DAOTAO + " dt ON kh." + DBHelper.COL_ID_KH + " = dt." + DBHelper.COL_ID_KH_FK + " " +
                "JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON dt." + DBHelper.COL_ID_NV_FK_DT + " = nv." + DBHelper.COL_ID_NV;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new TrainingDTO(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean insertTraining(String tenKH, String gv, String bd, String kt, String idNV, String ketQua) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String idKH = "KH" + System.currentTimeMillis();

            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_ID_KH, idKH);
            values.put(DBHelper.COL_TEN_KH, tenKH);
            values.put(DBHelper.COL_GIANG_VIEN, gv);
            values.put(DBHelper.COL_NGAY_BD, bd);
            values.put(DBHelper.COL_NGAY_KT, kt);
            db.insert(DBHelper.TABLE_KHOAHOC, null, values);

            ContentValues dtValues = new ContentValues();
            dtValues.put(DBHelper.COL_ID_KH_FK, idKH);
            dtValues.put(DBHelper.COL_ID_NV_FK_DT, idNV);
            dtValues.put(DBHelper.COL_KET_QUA, ketQua);
            db.insert(DBHelper.TABLE_CHITIET_DAOTAO, null, dtValues);

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateTraining(String idKH, String tenKH, String gv, String bd, String kt, String idNV, String ketQua) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues khValues = new ContentValues();
            khValues.put(DBHelper.COL_TEN_KH, tenKH);
            khValues.put(DBHelper.COL_GIANG_VIEN, gv);
            khValues.put(DBHelper.COL_NGAY_BD, bd);
            khValues.put(DBHelper.COL_NGAY_KT, kt);
            db.update(DBHelper.TABLE_KHOAHOC, khValues, DBHelper.COL_ID_KH + " = ?", new String[]{idKH});

            ContentValues dtValues = new ContentValues();
            dtValues.put(DBHelper.COL_KET_QUA, ketQua);
            db.update(DBHelper.TABLE_CHITIET_DAOTAO, dtValues,
                    DBHelper.COL_ID_KH_FK + " = ? AND " + DBHelper.COL_ID_NV_FK_DT + " = ?",
                    new String[]{idKH, idNV});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteTraining(String idKH, String idNV) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DBHelper.TABLE_CHITIET_DAOTAO,
                DBHelper.COL_ID_KH_FK + " = ? AND " + DBHelper.COL_ID_NV_FK_DT + " = ?",
                new String[]{idKH, idNV});
        return result > 0;
    }
}