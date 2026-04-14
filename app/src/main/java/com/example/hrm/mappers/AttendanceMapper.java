package com.example.hrm.mappers;

import android.database.Cursor;
import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Employee;
import com.example.hrm.dto.EmployeeAttendanceDTO;

public class AttendanceMapper {
    public static EmployeeAttendanceDTO fromCursor(Cursor c) {
        Employee emp = new Employee();
        emp.setIdNv(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
        emp.setMaNv(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
        emp.setHoTen(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
        emp.setAvatar(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_AVATAR)));

        EmployeeAttendanceDTO dto = new EmployeeAttendanceDTO();
        dto.setEmployee(emp);

        dto.setTenPhongBan(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_TEN_PB)));

        dto.setGioVao(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_GIO_VAO)));
        dto.setTrangThaiChamCong(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CC_TRANG_THAI)));

        return dto;
    }
}