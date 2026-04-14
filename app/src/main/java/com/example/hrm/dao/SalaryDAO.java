package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Salary;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {
    private final DBHelper dbHelper;
    private static final double LUONG_CO_BAN = 1800000.0;
    private static final double TIEN_CONG_MOI_NGAY = 250000.0;

    public SalaryDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    private String formatSearchDate(String thangNam) {
        if (thangNam == null || !thangNam.contains("/")) return thangNam;
        String[] parts = thangNam.split("/");
        return parts[1] + "-" + parts[0];
    }

    public List<Salary> getAllSalaries() {
        List<Salary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT l.*, nv.ma_nv, nv.ho_ten FROM " + DBHelper.TABLE_LUONG + " l " +
                "INNER JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON l." + DBHelper.COL_ID_NV + " = nv." + DBHelper.COL_ID_NV + " " +
                "ORDER BY l." + DBHelper.COL_THANG_NAM + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Salary salary = new Salary();
                salary.setIdLuong(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_LUONG)));
                salary.setIdNv(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                salary.setMaNv(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                salary.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                salary.setThangNam(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_THANG_NAM)));
                salary.setSoNgayCong(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_SO_NGAY_CONG)));
                salary.setPhuCap(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PHU_CAP)));
                salary.setKhauTru(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_KHAU_TRU)));
                salary.setTongThuong(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_TONG_THUONG)));
                salary.setTongPhat(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_TONG_PHAT)));
                salary.setTongLuong(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_TONG_LUONG)));
                list.add(salary);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public boolean insertSalary(Salary salary) {
        if (isSalaryExists(salary.getIdNv(), salary.getThangNam())) return false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int soNgayCong = getSoNgayCongByMonth(salary.getIdNv(), salary.getThangNam());
            double tongThuong = getTongThuongByMonth(salary.getIdNv(), salary.getThangNam());
            double tongPhat = getTongPhatByMonth(salary.getIdNv(), salary.getThangNam());
            double heSoLuong = getHeSoLuongByNhanVien(salary.getIdNv());

            double tongLuong = calculateTongLuong(heSoLuong, soNgayCong, salary.getPhuCap(),
                    salary.getKhauTru(), tongThuong, tongPhat);

            ContentValues v = new ContentValues();
            v.put(DBHelper.COL_ID_NV, salary.getIdNv());
            v.put(DBHelper.COL_THANG_NAM, salary.getThangNam());
            v.put(DBHelper.COL_SO_NGAY_CONG, soNgayCong);
            v.put(DBHelper.COL_PHU_CAP, salary.getPhuCap());
            v.put(DBHelper.COL_KHAU_TRU, salary.getKhauTru());
            v.put(DBHelper.COL_TONG_THUONG, tongThuong);
            v.put(DBHelper.COL_TONG_PHAT, tongPhat);
            v.put(DBHelper.COL_TONG_LUONG, tongLuong);

            return db.insert(DBHelper.TABLE_LUONG, null, v) != -1;
        } finally {
            db.close();
        }
    }

    private int getSoNgayCongByMonth(int idNv, String thangNam) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchDate = formatSearchDate(thangNam);
        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_CHUYENCAN +
                " WHERE " + DBHelper.COL_ID_NV_FK + " = ? " +
                " AND " + DBHelper.COL_NGAY_CC + " LIKE ? AND " + DBHelper.COL_CC_TRANG_THAI + " = 1";

        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(idNv), searchDate + "%"});
        int count = 0;
        if (c != null && c.moveToFirst()) count = c.getInt(0);
        if (c != null) c.close();
        return count;
    }

    private double getTongThuongByMonth(int idNv, String thangNam) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchDate = formatSearchDate(thangNam);
        String sql = "SELECT SUM(" + DBHelper.COL_SO_TIEN_THUONG + ") FROM " + DBHelper.TABLE_KHENTHUONG +
                " WHERE " + DBHelper.COL_ID_NV + " = ? AND " + DBHelper.COL_NGAY_QUYET_DINH + " LIKE ?";

        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(idNv), searchDate + "%"});
        double total = 0;
        if (c != null && c.moveToFirst()) total = c.getDouble(0);
        if (c != null) c.close();
        return total;
    }

    private double getTongPhatByMonth(int idNv, String thangNam) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchDate = formatSearchDate(thangNam);
        String sql = "SELECT SUM(" + DBHelper.COL_SO_TIEN_PHAT + ") FROM " + DBHelper.TABLE_KYLUAT +
                " WHERE " + DBHelper.COL_ID_NV + " = ? AND " + DBHelper.COL_NGAY_QUYET_DINH + " LIKE ?";

        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(idNv), searchDate + "%"});
        double total = 0;
        if (c != null && c.moveToFirst()) total = c.getDouble(0);
        if (c != null) c.close();
        return total;
    }

    private double getHeSoLuongByNhanVien(int idNv) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DBHelper.COL_HE_SO_LUONG + " FROM " + DBHelper.TABLE_NHANVIEN +
                " WHERE " + DBHelper.COL_ID_NV + " = ?", new String[]{String.valueOf(idNv)});
        double hs = 1.0;
        if (c != null && c.moveToFirst()) hs = c.getDouble(0);
        if (c != null) c.close();
        return hs;
    }

    private double calculateTongLuong(double heSoLuong, int soNgayCong, double phuCap, double khauTru, double tongThuong, double tongPhat) {
        return (LUONG_CO_BAN * heSoLuong) + (soNgayCong * TIEN_CONG_MOI_NGAY) + phuCap + tongThuong - khauTru - tongPhat;
    }

    public boolean isSalaryExists(int idNv, String thangNam) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + DBHelper.TABLE_LUONG + " WHERE id_nv=? AND thang_nam=?",
                new String[]{String.valueOf(idNv), thangNam});
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        return exists;
    }

    public boolean deleteSalary(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int res = db.delete(DBHelper.TABLE_LUONG, "id_luong=?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    public List<String> getAllEmployeeDisplayList() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id_nv, ma_nv, ho_ten FROM NhanVien", null);
        if (c != null && c.moveToFirst()) {
            do {
                list.add(c.getInt(0) + " - " + c.getString(1) + " - " + c.getString(2));
            } while (c.moveToNext());
        }
        if (c != null) c.close();
        return list;
    }

    public int extractEmployeeId(String s) {
        try { return Integer.parseInt(s.split(" - ")[0].trim()); } catch (Exception e) { return -1; }
    }

    public boolean updateSalary(Salary s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int nc = getSoNgayCongByMonth(s.getIdNv(), s.getThangNam());
            double tt = getTongThuongByMonth(s.getIdNv(), s.getThangNam());
            double tp = getTongPhatByMonth(s.getIdNv(), s.getThangNam());
            double hs = getHeSoLuongByNhanVien(s.getIdNv());
            double total = calculateTongLuong(hs, nc, s.getPhuCap(), s.getKhauTru(), tt, tp);

            ContentValues v = new ContentValues();
            v.put(DBHelper.COL_SO_NGAY_CONG, nc);
            v.put(DBHelper.COL_PHU_CAP, s.getPhuCap());
            v.put(DBHelper.COL_KHAU_TRU, s.getKhauTru());
            v.put(DBHelper.COL_TONG_THUONG, tt);
            v.put(DBHelper.COL_TONG_PHAT, tp);
            v.put(DBHelper.COL_TONG_LUONG, total);
            return db.update(DBHelper.TABLE_LUONG, v, "id_luong=?", new String[]{String.valueOf(s.getIdLuong())}) > 0;
        } finally { db.close(); }
    }
}