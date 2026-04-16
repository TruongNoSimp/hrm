package com.example.hrm.mappers;

import android.database.Cursor;
import com.example.hrm.database.DBHelper;
import com.example.hrm.dto.SalaryDTO;
import com.example.hrm.models.Salary;
import java.text.NumberFormat;
import java.util.Locale;

public class SalaryMapper {
    // Sử dụng Locale Việt Nam để định dạng tiền tệ: 1.000.000
    private static final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
     // Chuyển đổi từ Model (thực thể) sang DTO (hiển thị)

    public static SalaryDTO toDTO(Salary s) {
        if (s == null) return null;

        SalaryDTO dto = new SalaryDTO();
        dto.setIdLuong(s.getIdLuong());
        dto.setIdNv(s.getIdNv());
        dto.setMaNv(s.getMaNv());
        dto.setHoTen(s.getHoTen());

        // Tạo chuỗi hiển thị kết hợp cho Spinner hoặc Header
        dto.setNhanVienDisplay(s.getMaNv() + " - " + s.getHoTen());

        dto.setThangNam(s.getThangNam());
        dto.setSoNgayCong(s.getSoNgayCong());

        // Định dạng tiền tệ kèm đơn vị "đ"
        dto.setPhuCapDisplay(formatter.format(s.getPhuCap()) + " đ");
        dto.setKhauTruDisplay(formatter.format(s.getKhauTru()) + " đ");
        dto.setTongThuongDisplay(formatter.format(s.getTongThuong()) + " đ");
        dto.setTongPhatDisplay(formatter.format(s.getTongPhat()) + " đ");
        dto.setTongLuongDisplay(formatter.format(s.getTongLuong()) + " đ");

        // Lưu lại giá trị số nguyên bản để phục vụ logic Edit/Update
        dto.setPhuCapRaw(s.getPhuCap());
        dto.setKhauTruRaw(s.getKhauTru());

        return dto;
    }

     // Đọc dữ liệu trực tiếp từ Cursor của SQLite và chuyển thành Model Salary
    public static Salary fromCursor(Cursor c) {
        if (c == null) return null;

        Salary s = new Salary();
        s.setIdLuong(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_LUONG)));
        s.setIdNv(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID_NV)));
        s.setMaNv(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_MA_NV)));
        s.setHoTen(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_HO_TEN)));
        s.setThangNam(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_THANG_NAM)));
        s.setSoNgayCong(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SO_NGAY_CONG)));
        s.setPhuCap(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_PHU_CAP)));
        s.setKhauTru(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_KHAU_TRU)));
        s.setTongThuong(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_TONG_THUONG)));
        s.setTongPhat(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_TONG_PHAT)));
        s.setTongLuong(c.getDouble(c.getColumnIndexOrThrow(DBHelper.COL_TONG_LUONG)));

        return s;
    }
}