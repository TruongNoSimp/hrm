package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    private DBHelper dbHelper;

    public DepartmentDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM PhongBan", null);

        if (cursor.moveToFirst()) {
            do {
                Department d = new Department();
                d.setIdPhongBan(cursor.getInt(cursor.getColumnIndexOrThrow("id_phong_ban")));
                d.setMaPb(cursor.getString(cursor.getColumnIndexOrThrow("ma_pb")));
                d.setTenPhong(cursor.getString(cursor.getColumnIndexOrThrow("ten_phong")));
                d.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("mo_ta")));

                list.add(d);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public boolean isDepartmentCodeExists(String maPb) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM PhongBan WHERE ma_pb = ?",
                new String[]{maPb}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

    public boolean insertDepartment(Department d) {
        if (isDepartmentCodeExists(d.getMaPb())) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ma_pb", d.getMaPb());
        values.put("ten_phong", d.getTenPhong());
        values.put("mo_ta", d.getMoTa());

        long result = db.insert("PhongBan", null, values);

        db.close();
        return result != -1;
    }

    public boolean updateDepartment(Department d) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ten_phong", d.getTenPhong());
        values.put("mo_ta", d.getMoTa());

        int result = db.update(
                "PhongBan",
                values,
                "id_phong_ban = ?",
                new String[]{String.valueOf(d.getIdPhongBan())}
        );

        db.close();
        return result > 0;
    }

    public boolean hasEmployees(int idPhongBan) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM NhanVien WHERE id_phong_ban = ?",
                new String[]{String.valueOf(idPhongBan)}
        );

        boolean has = false;

        if (cursor.moveToFirst()) {
            has = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return has;
    }

    public boolean deleteDepartment(int idPhongBan) {
        if (hasEmployees(idPhongBan)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(
                "PhongBan",
                "id_phong_ban = ?",
                new String[]{String.valueOf(idPhongBan)}
        );

        db.close();
        return result > 0;
    }
}