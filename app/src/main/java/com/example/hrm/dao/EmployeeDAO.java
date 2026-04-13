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

        String sql = "SELECT nv.*, pb.ten_phong " +
                "FROM NhanVien nv " +
                "LEFT JOIN PhongBan pb ON nv.id_phong_ban_fk = pb.id_phong_ban " +
                "ORDER BY nv.id_nv DESC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Employee e = new Employee();
                e.setIdNv(cursor.getInt(cursor.getColumnIndexOrThrow("id_nv")));
                e.setMaNv(cursor.getString(cursor.getColumnIndexOrThrow("ma_nv")));
                e.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow("ho_ten")));
                e.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow("ngay_sinh")));
                e.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow("gioi_tinh")));
                e.setSoDt(cursor.getString(cursor.getColumnIndexOrThrow("so_dt")));
                e.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                e.setIdPhongBan(cursor.getInt(cursor.getColumnIndexOrThrow("id_phong_ban_fk")));
                e.setChucVu(cursor.getString(cursor.getColumnIndexOrThrow("chuc_vu")));
                e.setNgayVaoLam(cursor.getString(cursor.getColumnIndexOrThrow("ngay_vao_lam")));
                e.setHeSoLuong(cursor.getDouble(cursor.getColumnIndexOrThrow("he_so_luong")));
                e.setTrangThai(cursor.getInt(cursor.getColumnIndexOrThrow("trang_thai")));
                e.setTenPhongBan(cursor.getString(cursor.getColumnIndexOrThrow("ten_phong")));
                e.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow("avatar")));
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
        Cursor cursor = db.rawQuery("SELECT * FROM NhanVien WHERE ma_nv = ?", new String[]{maNv});
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
        values.put("ma_nv", e.getMaNv());
        values.put("ho_ten", e.getHoTen());
        values.put("ngay_sinh", e.getNgaySinh());
        values.put("gioi_tinh", e.getGioiTinh());
        values.put("so_dt", e.getSoDt());
        values.put("email", e.getEmail());
        values.put("id_phong_ban_fk", e.getIdPhongBan());
        values.put("chuc_vu", e.getChucVu());
        values.put("ngay_vao_lam", e.getNgayVaoLam());
        values.put("he_so_luong", e.getHeSoLuong());
        values.put("avatar", e.getAvatar());
        values.put("trang_thai", e.getTrangThai());

        long result = db.insert("NhanVien", null, values);
        db.close();
        return result != -1;
    }

    public boolean updateEmployee(Employee e) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ho_ten", e.getHoTen());
        values.put("ngay_sinh", e.getNgaySinh());
        values.put("gioi_tinh", e.getGioiTinh());
        values.put("so_dt", e.getSoDt());
        values.put("email", e.getEmail());
        values.put("id_phong_ban_fk", e.getIdPhongBan());
        values.put("chuc_vu", e.getChucVu());
        values.put("ngay_vao_lam", e.getNgayVaoLam());
        values.put("he_so_luong", e.getHeSoLuong());
        values.put("avatar", e.getAvatar());
        values.put("trang_thai", e.getTrangThai());

        int result = db.update(
                "NhanVien",
                values,
                "id_nv = ?",
                new String[]{String.valueOf(e.getIdNv())}
        );

        db.close();
        return result > 0;
    }

    public boolean deleteEmployee(int idNv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("NhanVien", "id_nv = ?", new String[]{String.valueOf(idNv)});
        db.close();
        return result > 0;
    }

    public int getEmployeeCountFromDB() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM NhanVien", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}