package com.example.hrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrm.db";
    private static final int DATABASE_VERSION = 7;

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
    public static final String COL_ID_PB_FK = "id_phong_ban"; // Foreign Key
    public static final String COL_CHUC_VU = "chuc_vu";
    public static final String COL_NGAY_VAO_LAM = "ngay_vao_lam";
    public static final String COL_HE_SO_LUONG = "he_so_luong";
    public static final String COL_TRANG_THAI = "trang_thai";
    public static final String COL_AVATAR = "avatar";

    //ChuyenCan (Attendance)
    public static final String TABLE_CHUYENCAN = "ChuyenCan";
    public static final String COL_ID_CC = "id_cc";
    public static final String COL_ID_NV_FK = "id_nv"; // Foreign Key
    public static final String COL_NGAY_CC = "ngay_cc";
    public static final String COL_GIO_VAO = "gio_vao";
    public static final String COL_GIO_RA = "gio_ra";
    public static final String COL_CC_TRANG_THAI = "trang_thai_cc";
    public static final String COL_GHI_CHU = "ghi_chu";

    //KyLuat (Discipline)
    public static final String TABLE_KYLUAT = "KyLuat";
    public static final String COL_ID_KYLUAT = "id_ky_luat";
    public static final String COL_NGAY_QUYET_DINH = "ngay_quyet_dinh";
    public static final String COL_HINH_THUC = "hinh_thuc";
    public static final String COL_SO_TIEN_PHAT = "so_tien_phat";
    public static final String COL_LY_DO = "ly_do";

    //KhenThuong (Reward)
    public static final String TABLE_KHENTHUONG = "KhenThuong";
    public static final String COL_ID_KHENTHUONG = "id_khen_thuong";
    public static final String COL_SO_TIEN_THUONG = "so_tien_thuong";

    //TaiKhoan (Account)
    public static final String TABLE_TAIKHOAN = "TaiKhoan";
    public static final String COL_ID_USER = "id_user";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

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
    private static final String[][] TAIKHOAN_SEED = {
            {"admin", "123456"},
            {"manager", "123456"}
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
            // Tạo bảng Phòng Ban
            String createPB = "CREATE TABLE " + TABLE_PHONGBAN + " ("
                    + COL_ID_PB + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MA_PB + " TEXT UNIQUE NOT NULL, "
                    + COL_TEN_PB + " TEXT NOT NULL, "
                    + COL_MO_TA + " TEXT)";
            db.execSQL(createPB);

            // Tạo bảng Nhân Viên
            String createNV = "CREATE TABLE " + TABLE_NHANVIEN + " ("
                    + COL_ID_NV + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MA_NV + " TEXT UNIQUE NOT NULL, "
                    + COL_HO_TEN + " TEXT NOT NULL, "
                    + COL_NGAY_SINH + " TEXT, "
                    + COL_GIOI_TINH + " TEXT, "
                    + COL_SDT + " TEXT, "
                    + COL_EMAIL + " TEXT, "
                    + COL_ID_PB_FK + " INTEGER, "
                    + COL_CHUC_VU + " TEXT, "
                    + COL_NGAY_VAO_LAM + " TEXT, "
                    + COL_HE_SO_LUONG + " REAL DEFAULT 1.0, "
                    + COL_TRANG_THAI + " INTEGER DEFAULT 1, "
                    + COL_AVATAR + " TEXT, "
                    + "FOREIGN KEY(" + COL_ID_PB_FK + ") REFERENCES " + TABLE_PHONGBAN + "(" + COL_ID_PB + ") "
                    + "ON DELETE SET NULL)";
            db.execSQL(createNV);

            // Tạo bảng Chuyên Cần
            String createCC = "CREATE TABLE " + TABLE_CHUYENCAN + " ("
                    + COL_ID_CC + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ID_NV_FK + " INTEGER NOT NULL, "
                    + COL_NGAY_CC + " TEXT NOT NULL, "
                    + COL_GIO_VAO + " TEXT, "
                    + COL_GIO_RA + " TEXT, "
                    + COL_CC_TRANG_THAI + " INTEGER, "
                    + COL_GHI_CHU + " TEXT, "
                    + "UNIQUE(" + COL_ID_NV_FK + ", " + COL_NGAY_CC + "), "
                    + "FOREIGN KEY(" + COL_ID_NV_FK + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") "
                    + "ON DELETE CASCADE)";
            db.execSQL(createCC);

            // Tạo bảng Kỷ Luật
            String createKL = "CREATE TABLE " + TABLE_KYLUAT + " ("
                    + COL_ID_KYLUAT + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ID_NV + " INTEGER NOT NULL, "
                    + COL_NGAY_QUYET_DINH + " TEXT, "
                    + COL_HINH_THUC + " TEXT, "
                    + COL_SO_TIEN_PHAT + " REAL DEFAULT 0, "
                    + COL_LY_DO + " TEXT, "
                    + "FOREIGN KEY(" + COL_ID_NV + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") "
                    + "ON DELETE CASCADE)";
            db.execSQL(createKL);

            // Tạo bảng Khen Thưởng
            String createKT = "CREATE TABLE " + TABLE_KHENTHUONG + " ("
                    + COL_ID_KHENTHUONG + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ID_NV + " INTEGER NOT NULL, "
                    + COL_NGAY_QUYET_DINH + " TEXT, "
                    + COL_HINH_THUC + " TEXT, "
                    + COL_SO_TIEN_THUONG + " REAL DEFAULT 0, "
                    + COL_LY_DO + " TEXT, "
                    + "FOREIGN KEY(" + COL_ID_NV + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") "
                    + "ON DELETE CASCADE)";
            db.execSQL(createKT);

            // Tạo bảng Tài Khoản
            String createTK = "CREATE TABLE " + TABLE_TAIKHOAN + " ("
                    + COL_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                    + COL_PASSWORD + " TEXT NOT NULL)";
            db.execSQL(createTK);

            seedInitialData(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed Phòng Ban
        for (String[] pb : PHONG_BAN_SEED) {
            db.execSQL(
                    "INSERT INTO " + TABLE_PHONGBAN + " (" +
                            COL_MA_PB + ", " +
                            COL_TEN_PB + ", " +
                            COL_MO_TA + ") VALUES (?, ?, ?)",
                    new Object[]{pb[0], pb[1], pb[2]}
            );
        }

        // Seed Tài Khoản
        for (String[] tk : TAIKHOAN_SEED) {
            db.execSQL(
                    "INSERT INTO " + TABLE_TAIKHOAN + " (" +
                            COL_USERNAME + ", " +
                            COL_PASSWORD + ") VALUES (?, ?)",
                    new Object[]{tk[0], tk[1]}
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYLUAT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KHENTHUONG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHUYENCAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHANVIEN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONGBAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAIKHOAN);
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}