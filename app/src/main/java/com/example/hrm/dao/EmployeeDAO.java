package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Department;
import com.example.hrm.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private final DBHelper dbHelper;

    public EmployeeDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT nv.*, pb." + DBHelper.COL_TEN_PB + " " +
                "FROM " + DBHelper.TABLE_NHANVIEN + " nv " +
                "LEFT JOIN " + DBHelper.TABLE_PHONGBAN + " pb ON nv." + DBHelper.COL_ID_PB_FK + " = pb." + DBHelper.COL_ID_PB + " " +
                "ORDER BY nv." + DBHelper.COL_ID_NV + " DESC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Employee e = new Employee();
                e.setIdNv(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                e.setMaNv(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                e.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                e.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NGAY_SINH)));
                e.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_GIOI_TINH)));
                e.setSoDt(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SDT)));
                e.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EMAIL)));
                e.setIdPhongBan(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_PB_FK)));
                e.setChucVu(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CHUC_VU)));
                e.setNgayVaoLam(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NGAY_VAO_LAM)));
                e.setHeSoLuong(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_HE_SO_LUONG)));
                e.setTrangThai(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_TRANG_THAI)));
                e.setTenPhongBan(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TEN_PB)));
                e.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_AVATAR)));
                list.add(e);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM PhongBan ORDER BY ten_phong ASC", null);

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

    public boolean isEmployeeCodeExists(String maNv) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NHANVIEN + " WHERE " + DBHelper.COL_MA_NV + " = ?", new String[]{maNv});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean insertEmployee(Employee e) {
        if (isEmployeeCodeExists(e.getMaNv())) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_MA_NV, e.getMaNv());
        values.put(DBHelper.COL_HO_TEN, e.getHoTen());
        values.put(DBHelper.COL_NGAY_SINH, e.getNgaySinh());
        values.put(DBHelper.COL_GIOI_TINH, e.getGioiTinh());
        values.put(DBHelper.COL_SDT, e.getSoDt());
        values.put(DBHelper.COL_EMAIL, e.getEmail());
        values.put(DBHelper.COL_ID_PB_FK, e.getIdPhongBan());
        values.put(DBHelper.COL_CHUC_VU, e.getChucVu());
        values.put(DBHelper.COL_NGAY_VAO_LAM, e.getNgayVaoLam());
        values.put(DBHelper.COL_HE_SO_LUONG, e.getHeSoLuong());
        values.put(DBHelper.COL_AVATAR, e.getAvatar());
        values.put(DBHelper.COL_TRANG_THAI, e.getTrangThai());

        long result = db.insert(DBHelper.TABLE_NHANVIEN, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateEmployee(Employee e) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_HO_TEN, e.getHoTen());
        values.put(DBHelper.COL_NGAY_SINH, e.getNgaySinh());
        values.put(DBHelper.COL_GIOI_TINH, e.getGioiTinh());
        values.put(DBHelper.COL_SDT, e.getSoDt());
        values.put(DBHelper.COL_EMAIL, e.getEmail());
        values.put(DBHelper.COL_ID_PB_FK, e.getIdPhongBan());
        values.put(DBHelper.COL_CHUC_VU, e.getChucVu());
        values.put(DBHelper.COL_NGAY_VAO_LAM, e.getNgayVaoLam());
        values.put(DBHelper.COL_HE_SO_LUONG, e.getHeSoLuong());
        values.put(DBHelper.COL_AVATAR, e.getAvatar());
        values.put(DBHelper.COL_TRANG_THAI, e.getTrangThai());

        int result = db.update(
                DBHelper.TABLE_NHANVIEN,
                values,
                DBHelper.COL_ID_NV + " = ?",
                new String[]{String.valueOf(e.getIdNv())}
        );

        db.close();
        return result > 0;
    }

    public boolean deleteEmployee(int idNv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DBHelper.TABLE_NHANVIEN, DBHelper.COL_ID_NV + " = ?", new String[]{String.valueOf(idNv)});
        db.close();
        return result > 0;
    }

    public int getEmployeeCountFromDB() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_NHANVIEN, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}