package com.example.hrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrm.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PHONGBAN = "PhongBan";
    public static final String COL_ID_PHONG_BAN = "id_phong_ban";
    public static final String COL_MA_PB = "ma_pb";
    public static final String COL_TEN_PHONG = "ten_phong";
    public static final String COL_MO_TA = "mo_ta";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPhongBanTable = "CREATE TABLE " + TABLE_PHONGBAN + " ("
                + COL_ID_PHONG_BAN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MA_PB + " TEXT UNIQUE, "
                + COL_TEN_PHONG + " TEXT NOT NULL, "
                + COL_MO_TA + " TEXT"
                + ")";
        db.execSQL(createPhongBanTable);

        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB01', 'Nhân sự', 'Quản lý hồ sơ và tuyển dụng')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB02', 'Kế toán', 'Quản lý tài chính và lương')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB03', 'Công nghệ thông tin', 'Quản lý hệ thống và phần mềm')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB04', 'Kinh doanh', 'Phụ trách doanh số và khách hàng')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB05', 'Marketing', 'Quảng bá thương hiệu và sản phẩm')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB06', 'Hành chính', 'Quản lý văn thư và tài sản')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB07', 'Chăm sóc khách hàng', 'Hỗ trợ và tiếp nhận phản hồi')");
        db.execSQL("INSERT INTO PhongBan (ma_pb, ten_phong, mo_ta) VALUES ('PB08', 'Kỹ thuật', 'Bảo trì và hỗ trợ kỹ thuật')");

        String CREATE_TABLE_NHAN_VIEN = "CREATE TABLE NhanVien (" +
                "id_nv INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ma_nv TEXT UNIQUE, " +
                "ho_ten TEXT NOT NULL, " +
                "ngay_sinh TEXT, " +
                "gioi_tinh TEXT, " +
                "so_dt TEXT, " +
                "email TEXT, " +
                "id_phong_ban INTEGER, " +
                "chuc_vu TEXT, " +
                "ngay_vao_lam TEXT, " +
                "he_so_luong REAL, " +
                "trang_thai INTEGER, " +
                "FOREIGN KEY(id_phong_ban) REFERENCES PhongBan(id_phong_ban)" +
                ")";
        db.execSQL(CREATE_TABLE_NHAN_VIEN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONGBAN);
        onCreate(db);
    }
}