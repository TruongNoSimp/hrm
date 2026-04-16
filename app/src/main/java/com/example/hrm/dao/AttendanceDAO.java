package com.example.hrm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hrm.database.DBHelper;
import com.example.hrm.dto.EmployeeAttendanceDTO;
import com.example.hrm.dto.AttendanceHistoryDTO;
import com.example.hrm.mappers.AttendanceMapper;
import com.example.hrm.models.Attendance;
import com.example.hrm.models.Employee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceDAO {
    private DBHelper dbHelper;
    private Context context;

    public AttendanceDAO(Context context) {

        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public long markAttendance(int idNv, String gioVao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new Date());

        SharedPreferences prefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String workShift = prefs.getString("work_shift", "08:00") + ":00";

        values.put(DBHelper.COL_ID_NV_FK, idNv);
        values.put(DBHelper.COL_GIO_VAO, gioVao);
        values.put(DBHelper.COL_NGAY_CC, currentDate);

        int trangThai = gioVao.compareTo(workShift) > 0 ? 2 : 1;
        values.put(DBHelper.COL_CC_TRANG_THAI, trangThai);

        //Update attendance to void conflict
        return db.insertWithOnConflict(DBHelper.TABLE_CHUYENCAN, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Employee> searchEmployees(String name) {
        List<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NHANVIEN +
                " WHERE " + DBHelper.COL_HO_TEN + " LIKE ?", new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                Employee emp = new Employee();
                emp.setIdNv(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                emp.setMaNv(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                emp.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                emp.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_AVATAR)));
                emp.setChucVu(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CHUC_VU)));

                list.add(emp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Attendance> getHistoryByDate(String date) {
        List<Attendance> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT cc.*, nv." + DBHelper.COL_HO_TEN + ", nv." + DBHelper.COL_AVATAR +
                " FROM " + DBHelper.TABLE_CHUYENCAN + " cc " +
                " INNER JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON cc." + DBHelper.COL_ID_NV_FK + " = nv." + DBHelper.COL_ID_NV +
                " WHERE cc." + DBHelper.COL_NGAY_CC + " = ?" +
                " ORDER BY cc." + DBHelper.COL_GIO_VAO + " DESC";

        Cursor c = db.rawQuery(sql, new String[]{date});

        if (c.moveToFirst()) {
            do {
                Attendance att = new Attendance();
                // Đọc dữ liệu từ Cursor và đóng gói vào Model
                att.setIdCc(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_CC)));
                att.setIdNv(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV_FK)));
                att.setNgay(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NGAY_CC)));
                att.setGioVao(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_GIO_VAO)));
                att.setTrangThai(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CC_TRANG_THAI)));

                att.setGhiChu(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));

                list.add(att);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<EmployeeAttendanceDTO> getAllEmployeesWithAttendance() {
        List<EmployeeAttendanceDTO> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String sql = "SELECT nv.*, pb." + DBHelper.COL_TEN_PB +
                ", cc." + DBHelper.COL_GIO_VAO +
                ", cc." + DBHelper.COL_CC_TRANG_THAI +
                " FROM " + DBHelper.TABLE_NHANVIEN + " nv " +
                " LEFT JOIN " + DBHelper.TABLE_PHONGBAN + " pb ON nv." + DBHelper.COL_ID_PB_FK + " = pb." + DBHelper.COL_ID_PB +
                " LEFT JOIN " + DBHelper.TABLE_CHUYENCAN + " cc ON nv." + DBHelper.COL_ID_NV + " = cc." + DBHelper.COL_ID_NV_FK +
                " AND cc." + DBHelper.COL_NGAY_CC + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{today});

        if (c.moveToFirst()) {
            do {
                EmployeeAttendanceDTO dto = AttendanceMapper.fromCursor(c);
                list.add(dto);

            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public List<AttendanceHistoryDTO> getHistoryByDateWithDTO(String date) {
        List<AttendanceHistoryDTO> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT cc.*, nv." + DBHelper.COL_ID_NV + ", nv." + DBHelper.COL_MA_NV +
                ", nv." + DBHelper.COL_HO_TEN + ", nv." + DBHelper.COL_AVATAR +
                " FROM " + DBHelper.TABLE_CHUYENCAN + " cc " +
                " INNER JOIN " + DBHelper.TABLE_NHANVIEN + " nv ON cc." + DBHelper.COL_ID_NV_FK + " = nv." + DBHelper.COL_ID_NV +
                " WHERE cc." + DBHelper.COL_NGAY_CC + " = ?" +
                " ORDER BY cc." + DBHelper.COL_GIO_VAO + " DESC";

        Cursor c = db.rawQuery(sql, new String[]{date});

        if (c.moveToFirst()) {
            do {
                // Tạo Attendance object
                Attendance att = new Attendance();
                att.setIdCc(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_CC)));
                att.setIdNv(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV_FK)));
                att.setNgay(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NGAY_CC)));
                att.setGioVao(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_GIO_VAO)));
                att.setTrangThai(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CC_TRANG_THAI)));

                // Tạo Employee object
                Employee emp = new Employee();
                emp.setIdNv(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
                emp.setMaNv(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
                emp.setHoTen(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
                emp.setAvatar(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_AVATAR)));

                // Tạo DTO
                AttendanceHistoryDTO dto = new AttendanceHistoryDTO(att, emp);
                list.add(dto);
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public int getAttendanceCountToday() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_CHUYENCAN +
                " WHERE " + DBHelper.COL_NGAY_CC + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{today});

        if (c.moveToFirst()) {
            count = c.getInt(0);
        }

        c.close();
        return count;
    }
}
