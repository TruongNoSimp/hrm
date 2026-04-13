package com.example.hrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrm.db";
    private static final int DATABASE_VERSION = 6;

    // Table names
    public static final String TABLE_PHONG_BAN = "PhongBan";
    public static final String TABLE_NHAN_VIEN = "NhanVien";
    public static final String TABLE_KY_LUAT = "KyLuat";
    public static final String TABLE_KHEN_THUONG = "KhenThuong";

    // PhongBan columns
    public static final String COL_ID_PHONG_BAN = "id_phong_ban";
    public static final String COL_MA_PB = "ma_pb";
    public static final String COL_TEN_PHONG = "ten_phong";
    public static final String COL_MO_TA = "mo_ta";

    // NhanVien columns
    public static final String COL_ID_NV = "id_nv";
    public static final String COL_MA_NV = "ma_nv";
    public static final String COL_HO_TEN = "ho_ten";
    public static final String COL_NGAY_SINH = "ngay_sinh";
    public static final String COL_GIOI_TINH = "gioi_tinh";
    public static final String COL_SO_DT = "so_dt";
    public static final String COL_EMAIL = "email";
    public static final String COL_CHUC_VU = "chuc_vu";
    public static final String COL_NGAY_VAO_LAM = "ngay_vao_lam";
    public static final String COL_HE_SO_LUONG = "he_so_luong";
    public static final String COL_TRANG_THAI = "trang_thai";
    public static final String COL_AVATAR = "avatar";

    // KyLuat columns
    public static final String COL_ID_KY_LUAT = "id_ky_luat";
    public static final String COL_NGAY_QUYET_DINH = "ngay_quyet_dinh";
    public static final String COL_HINH_THUC = "hinh_thuc";
    public static final String COL_SO_TIEN_PHAT = "so_tien_phat";
    public static final String COL_LY_DO = "ly_do";

    // KhenThuong columns
    public static final String COL_ID_KHEN_THUONG = "id_khen_thuong";
    public static final String COL_SO_TIEN_THUONG = "so_tien_thuong";

    private static final String CREATE_TABLE_PHONG_BAN =
            "CREATE TABLE " + TABLE_PHONG_BAN + " (" +
                    COL_ID_PHONG_BAN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MA_PB + " TEXT UNIQUE NOT NULL, " +
                    COL_TEN_PHONG + " TEXT NOT NULL, " +
                    COL_MO_TA + " TEXT" +
                    ")";

    private static final String CREATE_TABLE_NHAN_VIEN =
            "CREATE TABLE " + TABLE_NHAN_VIEN + " (" +
                    COL_ID_NV + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MA_NV + " TEXT UNIQUE NOT NULL, " +
                    COL_HO_TEN + " TEXT NOT NULL, " +
                    COL_NGAY_SINH + " TEXT, " +
                    COL_GIOI_TINH + " TEXT, " +
                    COL_SO_DT + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_ID_PHONG_BAN + " INTEGER, " +
                    COL_CHUC_VU + " TEXT, " +
                    COL_NGAY_VAO_LAM + " TEXT, " +
                    COL_HE_SO_LUONG + " REAL DEFAULT 1.0, " +
                    COL_TRANG_THAI + " INTEGER DEFAULT 1, " +
                    COL_AVATAR + " TEXT, " +
                    "FOREIGN KEY(" + COL_ID_PHONG_BAN + ") REFERENCES " +
                    TABLE_PHONG_BAN + "(" + COL_ID_PHONG_BAN + ")" +
                    ")";

    private static final String CREATE_TABLE_KY_LUAT =
            "CREATE TABLE " + TABLE_KY_LUAT + " (" +
                    COL_ID_KY_LUAT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ID_NV + " INTEGER NOT NULL, " +
                    COL_NGAY_QUYET_DINH + " TEXT, " +
                    COL_HINH_THUC + " TEXT, " +
                    COL_SO_TIEN_PHAT + " REAL DEFAULT 0, " +
                    COL_LY_DO + " TEXT, " +
                    "FOREIGN KEY(" + COL_ID_NV + ") REFERENCES " +
                    TABLE_NHAN_VIEN + "(" + COL_ID_NV + ")" +
                    ")";

    private static final String CREATE_TABLE_KHEN_THUONG =
            "CREATE TABLE " + TABLE_KHEN_THUONG + " (" +
                    COL_ID_KHEN_THUONG + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ID_NV + " INTEGER NOT NULL, " +
                    COL_NGAY_QUYET_DINH + " TEXT, " +
                    COL_HINH_THUC + " TEXT, " +
                    COL_SO_TIEN_THUONG + " REAL DEFAULT 0, " +
                    COL_LY_DO + " TEXT, " +
                    "FOREIGN KEY(" + COL_ID_NV + ") REFERENCES " +
                    TABLE_NHAN_VIEN + "(" + COL_ID_NV + ")" +
                    ")";

    private static final String[][] PHONG_BAN_SEED = {
            {"PB01", "Nhân sự", "Quản lý hồ sơ và tuyển dụng"},
            {"PB02", "Kế toán", "Quản lý tài chính và lương"},
            {"PB03", "Công nghệ thông tin", "Quản lý hệ thống và phần mềm"},
            {"PB04", "Kinh doanh", "Phụ trách doanh số và khách hàng"},
            {"PB05", "Marketing", "Quảng bá thương hiệu và sản phẩm"},
            {"PB06", "Hành chính", "Quản lý văn thư và tài sản"},
            {"PB07", "Chăm sóc khách hàng", "Hỗ trợ và tiếp nhận phản hồi"},
            {"PB08", "Kỹ thuật", "Bảo trì và hỗ trợ kỹ thuật"}
    };

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
        db.beginTransaction();
        try {
            db.execSQL(CREATE_TABLE_PHONG_BAN);
            db.execSQL(CREATE_TABLE_NHAN_VIEN);
            db.execSQL(CREATE_TABLE_KY_LUAT);
            db.execSQL(CREATE_TABLE_KHEN_THUONG);

            seedPhongBan(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void seedPhongBan(SQLiteDatabase db) {
        for (String[] pb : PHONG_BAN_SEED) {
            db.execSQL(
                    "INSERT INTO " + TABLE_PHONG_BAN + " (" +
                            COL_MA_PB + ", " +
                            COL_TEN_PHONG + ", " +
                            COL_MO_TA + ") VALUES (?, ?, ?)",
                    new Object[]{pb[0], pb[1], pb[2]}
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KY_LUAT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KHEN_THUONG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHAN_VIEN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONG_BAN);

            onCreate(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}