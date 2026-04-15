package com.example.hrm.dto;
import java.io.Serializable;

public class SalaryDTO implements Serializable {
    private int idLuong;
    private int idNv;
    private String maNv;
    private String hoTen;
    private String nhanVienDisplay;
    private String thangNam;
    private int soNgayCong;

    private String phuCapDisplay;
    private String khauTruDisplay;
    private String tongThuongDisplay;
    private String tongPhatDisplay;
    private String tongLuongDisplay;

    private double phuCapRaw;
    private double khauTruRaw;

    public SalaryDTO() {}

    public int getIdLuong() { return idLuong; }
    public void setIdLuong(int idLuong) { this.idLuong = idLuong; }

    public int getIdNv() { return idNv; }
    public void setIdNv(int idNv) { this.idNv = idNv; }

    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getNhanVienDisplay() { return nhanVienDisplay; }
    public void setNhanVienDisplay(String nhanVienDisplay) { this.nhanVienDisplay = nhanVienDisplay; }

    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }

    public int getSoNgayCong() { return soNgayCong; }
    public void setSoNgayCong(int soNgayCong) { this.soNgayCong = soNgayCong; }

    public String getPhuCapDisplay() { return phuCapDisplay; }
    public void setPhuCapDisplay(String phuCapDisplay) { this.phuCapDisplay = phuCapDisplay; }

    public String getKhauTruDisplay() { return khauTruDisplay; }
    public void setKhauTruDisplay(String khauTruDisplay) { this.khauTruDisplay = khauTruDisplay; }

    public String getTongThuongDisplay() { return tongThuongDisplay; }
    public void setTongThuongDisplay(String tongThuongDisplay) { this.tongThuongDisplay = tongThuongDisplay; }

    public String getTongPhatDisplay() { return tongPhatDisplay; }
    public void setTongPhatDisplay(String tongPhatDisplay) { this.tongPhatDisplay = tongPhatDisplay; }

    public String getTongLuongDisplay() { return tongLuongDisplay; }
    public void setTongLuongDisplay(String tongLuongDisplay) { this.tongLuongDisplay = tongLuongDisplay; }

    public double getPhuCapRaw() { return phuCapRaw; }
    public void setPhuCapRaw(double phuCapRaw) { this.phuCapRaw = phuCapRaw; }

    public double getKhauTruRaw() { return khauTruRaw; }
    public void setKhauTruRaw(double khauTruRaw) { this.khauTruRaw = khauTruRaw; }
}
