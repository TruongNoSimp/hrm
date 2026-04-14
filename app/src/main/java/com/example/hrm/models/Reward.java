package com.example.hrm.models;

public class Reward {
    private int idKhenThuong;
    private int idNhanVien;
    private String ngayQuyetDinh;
    private String hinhThuc;
    private double soTienThuong;
    private String lyDo;

    private String tenNhanVien;
    private String maNhanVien;

    public Reward() {
    }

    public Reward(int idKhenThuong, int idNhanVien, String ngayQuyetDinh,
                  String hinhThuc, double soTienThuong, String lyDo) {
        this.idKhenThuong = idKhenThuong;
        this.idNhanVien = idNhanVien;
        this.ngayQuyetDinh = ngayQuyetDinh;
        this.hinhThuc = hinhThuc;
        this.soTienThuong = soTienThuong;
        this.lyDo = lyDo;
    }

    public int getIdKhenThuong() {
        return idKhenThuong;
    }

    public void setIdKhenThuong(int idKhenThuong) {
        this.idKhenThuong = idKhenThuong;
    }

    public int getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getNgayQuyetDinh() {
        return ngayQuyetDinh;
    }

    public void setNgayQuyetDinh(String ngayQuyetDinh) {
        this.ngayQuyetDinh = ngayQuyetDinh;
    }

    public String getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(String hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public double getSoTienThuong() {
        return soTienThuong;
    }

    public void setSoTienThuong(double soTienThuong) {
        this.soTienThuong = soTienThuong;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
}