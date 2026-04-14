package com.example.hrm.models;

public class Salary {
    private int idLuong;
    private int idNv;
    private String maNv;
    private String hoTen;
    private String thangNam;
    private int soNgayCong;
    private double phuCap;
    private double khauTru;
    private double tongThuong;
    private double tongPhat;
    private double tongLuong;

    public Salary() {
    }

    public Salary(int idLuong, int idNv, String maNv, String hoTen, String thangNam,
                  int soNgayCong, double phuCap, double khauTru,
                  double tongThuong, double tongPhat, double tongLuong) {
        this.idLuong = idLuong;
        this.idNv = idNv;
        this.maNv = maNv;
        this.hoTen = hoTen;
        this.thangNam = thangNam;
        this.soNgayCong = soNgayCong;
        this.phuCap = phuCap;
        this.khauTru = khauTru;
        this.tongThuong = tongThuong;
        this.tongPhat = tongPhat;
        this.tongLuong = tongLuong;
    }

    public int getIdLuong() {
        return idLuong;
    }

    public void setIdLuong(int idLuong) {
        this.idLuong = idLuong;
    }

    public int getIdNv() {
        return idNv;
    }

    public void setIdNv(int idNv) {
        this.idNv = idNv;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getThangNam() {
        return thangNam;
    }

    public void setThangNam(String thangNam) {
        this.thangNam = thangNam;
    }

    public int getSoNgayCong() {
        return soNgayCong;
    }

    public void setSoNgayCong(int soNgayCong) {
        this.soNgayCong = soNgayCong;
    }

    public double getPhuCap() {
        return phuCap;
    }

    public void setPhuCap(double phuCap) {
        this.phuCap = phuCap;
    }

    public double getKhauTru() {
        return khauTru;
    }

    public void setKhauTru(double khauTru) {
        this.khauTru = khauTru;
    }

    public double getTongThuong() {
        return tongThuong;
    }

    public void setTongThuong(double tongThuong) {
        this.tongThuong = tongThuong;
    }

    public double getTongPhat() {
        return tongPhat;
    }

    public void setTongPhat(double tongPhat) {
        this.tongPhat = tongPhat;
    }

    public double getTongLuong() {
        return tongLuong;
    }

    public void setTongLuong(double tongLuong) {
        this.tongLuong = tongLuong;
    }
}