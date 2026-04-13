package com.example.hrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrm.db";
    private static final int DATABASE_VERSION = 6;


    //PhongBan
    public static final String TABLE_PHONGBAN = "PhongBan";
    public static final String COL_ID_PB = "id_phong_ban";
    public static final String COL_MA_PB = "ma_pb";
    public static final String COL_TEN_PB = "ten_phong";
    public static final String COL_MO_TA = "mo_ta";

    //NhanVien
    public static final String TABLE_NHANVIEN = "NhanVien";
    public static final String COL_ID_NV = "id_nv";
    public static final String COL_MA_NV = "ma_nv";
    public static final String COL_HO_TEN = "ho_ten";
    public static final String COL_NGAY_SINH = "ngay_sinh";
    public static final String COL_GIOI_TINH = "gioi_tinh";
    public static final String COL_SDT = "so_dt";
    public static final String COL_EMAIL = "email";
    public static final String COL_ID_PB_FK = "id_phong_ban_fk"; // Foreign Key
    public static final String COL_CHUC_VU = "chuc_vu";
    public static final String COL_NGAY_VAO_LAM = "ngay_vao_lam";
    public static final String COL_HE_SO_LUONG = "he_so_luong";
    public static final String COL_TRANG_THAI = "trang_thai";
    public static final String COL_AVATAR = "avatar";

    //ChuyenCan
    public static final String TABLE_CHUYENCAN = "ChuyenCan";
    public static final String COL_ID_CC = "id_cc";
    public static final String COL_ID_NV_FK = "id_nv_fk"; // Foreign Key
    public static final String COL_NGAY_CC = "ngay_cc";
    public static final String COL_GIO_VAO = "gio_vao";
    public static final String COL_GIO_RA = "gio_ra";
    public static final String COL_CC_TRANG_THAI = "trang_thai_cc";
    public static final String COL_GHI_CHU = "ghi_chu";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPB = "CREATE TABLE " + TABLE_PHONGBAN + " ("
                + COL_ID_PB + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MA_PB + " TEXT UNIQUE, "
                + COL_TEN_PB + " TEXT NOT NULL, "
                + COL_MO_TA + " TEXT)";
        db.execSQL(createPB);

        // --- Tạo bảng Nhân Viên ---
        String createNV = "CREATE TABLE " + TABLE_NHANVIEN + " ("
                + COL_ID_NV + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MA_NV + " TEXT UNIQUE, "
                + COL_HO_TEN + " TEXT NOT NULL, "
                + COL_NGAY_SINH + " TEXT, "
                + COL_GIOI_TINH + " TEXT, "
                + COL_SDT + " TEXT, "
                + COL_EMAIL + " TEXT, "
                + COL_ID_PB_FK + " INTEGER, "
                + COL_CHUC_VU + " TEXT, "
                + COL_NGAY_VAO_LAM + " TEXT, "
                + COL_HE_SO_LUONG + " REAL, "
                + COL_TRANG_THAI + " INTEGER, "
                + COL_AVATAR + " TEXT, "
                + "FOREIGN KEY(" + COL_ID_PB_FK + ") REFERENCES " + TABLE_PHONGBAN + "(" + COL_ID_PB + ") "
                + "ON DELETE SET NULL)";
        db.execSQL(createNV);

        // --- Tạo bảng Chuyên Cần ---
        String createCC = "CREATE TABLE " + TABLE_CHUYENCAN + " ("
                + COL_ID_CC + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ID_NV_FK + " INTEGER, "
                + COL_NGAY_CC + " TEXT NOT NULL, "
                + COL_GIO_VAO + " TEXT, "
                + COL_GIO_RA + " TEXT, "
                + COL_CC_TRANG_THAI + " INTEGER, " // 0: Vắng, 1: Có mặt, 2: Muộn
                + COL_GHI_CHU + " TEXT, "
                + "UNIQUE(" + COL_ID_NV_FK + ", " + COL_NGAY_CC + "), " // Chặn điểm danh 2 lần/ngày
                + "FOREIGN KEY(" + COL_ID_NV_FK + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") "
                + "ON DELETE CASCADE)";
        db.execSQL(createCC);

        seedInitialData(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_PHONGBAN + " (" + COL_MA_PB + ", " + COL_TEN_PB + ") VALUES ('PB01', 'Nhân sự')");
        db.execSQL("INSERT INTO " + TABLE_PHONGBAN + " (" + COL_MA_PB + ", " + COL_TEN_PB + ") VALUES ('PB02', 'Công nghệ thông tin')");
        db.execSQL("INSERT INTO " + TABLE_PHONGBAN + " (" + COL_MA_PB + ", " + COL_TEN_PB + ") VALUES ('PB03', 'Kinh doanh')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHUYENCAN);
            String createCC = "CREATE TABLE " + TABLE_CHUYENCAN + " ("
                    + COL_ID_CC + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ID_NV_FK + " INTEGER, "
                    + COL_NGAY_CC + " TEXT NOT NULL, "
                    + COL_GIO_VAO + " TEXT, "
                    + COL_GIO_RA + " TEXT, "
                    + COL_CC_TRANG_THAI + " INTEGER, "
                    + COL_GHI_CHU + " TEXT, "
                    + "UNIQUE(" + COL_ID_NV_FK + ", " + COL_NGAY_CC + "), "
                    + "FOREIGN KEY(" + COL_ID_NV_FK + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") ON DELETE CASCADE)";
            db.execSQL(createCC);
        }
    }
}