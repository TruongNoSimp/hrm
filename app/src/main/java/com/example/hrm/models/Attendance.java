package com.example.hrm.models;

public class Attendance {
    private int idCc;
    private int idNv;
    private String ngay;
    private String gioVao;
    private String gioRa;
    private int trangThai;
    private String ghiChu;

    public Attendance() {
    }

    public Attendance(int idNv, String ngay, String gioVao, String gioRa, int trangThai, String ghiChu) {
        this.idNv = idNv;
        this.ngay = ngay;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public Attendance(int idCc, int idNv, String ngay, String gioVao, String gioRa, int trangThai, String ghiChu) {
        this.idCc = idCc;
        this.idNv = idNv;
        this.ngay = ngay;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }


    public int getIdCc() {
        return idCc;
    }

    public void setIdCc(int idCc) {
        this.idCc = idCc;
    }

    public int getIdNv() {
        return idNv;
    }

    public void setIdNv(int idNv) {
        this.idNv = idNv;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getGioVao() {
        return gioVao;
    }

    public void setGioVao(String gioVao) {
        this.gioVao = gioVao;
    }

    public String getGioRa() {
        return gioRa;
    }

    public void setGioRa(String gioRa) {
        this.gioRa = gioRa;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
