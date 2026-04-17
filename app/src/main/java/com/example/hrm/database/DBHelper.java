package com.example.hrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrm.db";
    private static final int DATABASE_VERSION = 12;

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
    public static final String COL_ADMINNAME = "adminName";

    //Luong (Salary)
    public static final String TABLE_LUONG = "Luong";
    public static final String COL_ID_LUONG = "id_luong";
    public static final String COL_THANG_NAM = "thang_nam";
    public static final String COL_SO_NGAY_CONG = "so_ngay_cong";
    public static final String COL_PHU_CAP = "phu_cap";
    public static final String COL_KHAU_TRU = "khau_tru";
    public static final String COL_TONG_THUONG = "tong_thuong";
    public static final String COL_TONG_PHAT = "tong_phat";
    public static final String COL_TONG_LUONG = "tong_luong";

    // Bảng KhoaHoc
    public static final String TABLE_KHOAHOC = "KhoaHoc";
    public static final String COL_ID_KH = "id_kh";
    public static final String COL_TEN_KH = "ten_kh";
    public static final String COL_NGAY_BD = "ngay_bat_dau";
    public static final String COL_NGAY_KT = "ngay_ket_thuc";
    public static final String COL_GIANG_VIEN = "giang_vien";

    // Bảng ChiTietDaoTao
    public static final String TABLE_CHITIET_DAOTAO = "ChiTietDaoTao";
    public static final String COL_ID_KH_FK = "id_kh";
    public static final String COL_ID_NV_FK_DT = "id_nv";
    public static final String COL_KET_QUA = "ket_qua";

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

    private static final String[][] NHAN_VIEN_SEED = {
            {"NV001", "Trần Hoàng Bách", "1990-01-05", "Nam", "0912000001", "bachth@gmail.com", "1", "Giám đốc", "2020-01-01", "5.0", "1"},
            {"NV002", "Lê Hoài Nam", "1995-03-12", "Nam", "0912000002", "namlh@gmail.com", "3", "Trưởng phòng CNTT", "2023-01-10", "4.0", "1"},
            {"NV003", "Nguyễn Minh Tuyết", "1998-11-25", "Nữ", "0912000003", "tuyetnm@gmail.com", "2", "Kế toán trưởng", "2023-05-20", "3.5", "1"},
            {"NV004", "Phạm Thu Hà", "2000-08-14", "Nữ", "0912000004", "hapt@gmail.com", "4", "Trưởng nhóm Sales", "2024-02-15", "2.8", "1"},
            {"NV005", "Vũ Đức Anh", "1997-12-30", "Nam", "0912000005", "anhvd@gmail.com", "8", "Kỹ thuật viên", "2023-09-12", "2.5", "1"},
            {"NV006", "Đặng Thùy Chi", "1999-04-22", "Nữ", "0912000006", "chidt@gmail.com", "5", "Chuyên viên Marketing", "2023-10-01", "2.2", "1"},
            {"NV007", "Bùi Quang Vinh", "1994-06-18", "Nam", "0912000007", "vinhbq@gmail.com", "3", "Lập trình viên", "2022-11-11", "3.2", "1"},
            {"NV008", "Đỗ Mỹ Linh", "2001-02-28", "Nữ", "0912000008", "linhdm@gmail.com", "7", "Tư vấn viên", "2024-03-01", "2.0", "1"},
            {"NV009", "Ngô Tiến Dũng", "1992-09-09", "Nam", "0912000009", "dungnt@gmail.com", "6", "Phó phòng Hành chính", "2021-05-20", "3.8", "1"},
            {"NV010", "Hoàng Bảo Ngọc", "1996-05-05", "Nữ", "0912000010", "ngochb@gmail.com", "4", "Nhân viên kinh doanh", "2022-04-15", "2.4", "1"}
    };
    private static final String[][] TAIKHOAN_SEED = {
            {"ducpa", "123456", "Phạm Anh Đức"},
            {"truongpm", "123456", "Phùng Minh Trường"},
            {"baodg", "123456", "Đinh Gia Bảo"},
            {"anhnv", "123456", "Nguyễn Văn Anh"}
    };

    private static final String[][] KHOA_HOC_SEED = {
            {"Lập trình Java Spring Boot nâng cao", "2026-05-01", "2026-05-15", "GS.TS. Trương Văn Nam"},
            {"Quản trị nhân sự trong kỷ nguyên AI", "2026-06-10", "2026-06-25", "ThS. Nguyễn Quản Lý"},
            {"Kỹ năng giao tiếp và xử lý từ chối", "2026-04-01", "2026-04-10", "Chuyên gia Trần Bách"}
    };

    private static final String[][] DAO_TAO_SEED = {
            {"1", "2", "Đạt"},
            {"1", "7", "Đang học"},
            {"2", "1", "Xuất sắc"},
            {"2", "9", "Đang học"},
            {"3", "4", "Không đạt"},
            {"3", "10", "Đạt"}
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
            String createPB = "CREATE TABLE " + TABLE_PHONGBAN + " ("
                    + COL_ID_PB + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MA_PB + " TEXT UNIQUE NOT NULL, "
                    + COL_TEN_PB + " TEXT NOT NULL, "
                    + COL_MO_TA + " TEXT)";
            db.execSQL(createPB);

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

            String createLuong = "CREATE TABLE " + TABLE_LUONG + " ("
                    + COL_ID_LUONG + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ID_NV + " INTEGER NOT NULL, "
                    + COL_THANG_NAM + " TEXT NOT NULL, "
                    + COL_SO_NGAY_CONG + " INTEGER DEFAULT 0, "
                    + COL_PHU_CAP + " REAL DEFAULT 0, "
                    + COL_KHAU_TRU + " REAL DEFAULT 0, "
                    + COL_TONG_THUONG + " REAL DEFAULT 0, "
                    + COL_TONG_PHAT + " REAL DEFAULT 0, "
                    + COL_TONG_LUONG + " REAL DEFAULT 0, "
                    + "UNIQUE(" + COL_ID_NV + ", " + COL_THANG_NAM + "), "
                    + "FOREIGN KEY(" + COL_ID_NV + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") "
                    + "ON DELETE CASCADE)";
            db.execSQL(createLuong);

            String createTK = "CREATE TABLE " + TABLE_TAIKHOAN + " ("
                    + COL_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                    + COL_PASSWORD + " TEXT NOT NULL, "
                    + COL_ADMINNAME + " TEXT)";
            db.execSQL(createTK);

            String createKH = "CREATE TABLE " + TABLE_KHOAHOC + " ("
                    + COL_ID_KH + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TEN_KH + " TEXT NOT NULL, "
                    + COL_NGAY_BD + " TEXT, "
                    + COL_NGAY_KT + " TEXT, "
                    + COL_GIANG_VIEN + " TEXT)";
            db.execSQL(createKH);

            String createCTDT = "CREATE TABLE " + TABLE_CHITIET_DAOTAO + " ("
                    + COL_ID_KH_FK + " INTEGER, "
                    + COL_ID_NV_FK_DT + " INTEGER, "
                    + COL_KET_QUA + " TEXT, "
                    + "PRIMARY KEY (" + COL_ID_KH_FK + ", " + COL_ID_NV_FK_DT + "), "
                    + "FOREIGN KEY(" + COL_ID_KH_FK + ") REFERENCES " + TABLE_KHOAHOC + "(" + COL_ID_KH + ") ON DELETE CASCADE, "
                    + "FOREIGN KEY(" + COL_ID_NV_FK_DT + ") REFERENCES " + TABLE_NHANVIEN + "(" + COL_ID_NV + ") ON DELETE CASCADE)";
            db.execSQL(createCTDT);

            seedInitialData(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void seedInitialData(SQLiteDatabase db) {
        for (String[] pb : PHONG_BAN_SEED) {
            db.execSQL(
                    "INSERT INTO " + TABLE_PHONGBAN + " (" +
                            COL_MA_PB + ", " +
                            COL_TEN_PB + ", " +
                            COL_MO_TA + ") VALUES (?, ?, ?)",
                    new Object[]{pb[0], pb[1], pb[2]}
            );
        }

        for (String[] tk : TAIKHOAN_SEED) {
            db.execSQL("INSERT INTO " + TABLE_TAIKHOAN + " ("
                            + COL_USERNAME + ", "
                            + COL_PASSWORD + ", "
                            + COL_ADMINNAME
                            + ") VALUES (?, ?, ?)",
                    new Object[]{tk[0], tk[1], tk[2]});
        }

        for (String[] nv : NHAN_VIEN_SEED) {
            db.execSQL("INSERT INTO " + TABLE_NHANVIEN + " ("
                            + COL_MA_NV + ", "
                            + COL_HO_TEN + ", "
                            + COL_NGAY_SINH + ", "
                            + COL_GIOI_TINH + ", "
                            + COL_SDT + ", "
                            + COL_EMAIL + ", "
                            + COL_ID_PB_FK + ", "
                            + COL_CHUC_VU + ", "
                            + COL_NGAY_VAO_LAM + ", "
                            + COL_HE_SO_LUONG + ", "
                            + COL_TRANG_THAI
                            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{nv[0], nv[1], nv[2], nv[3], nv[4], nv[5],
                            Integer.parseInt(nv[6]), nv[7], nv[8],
                            Double.parseDouble(nv[9]), Integer.parseInt(nv[10])});
        }

        for (String[] kh : KHOA_HOC_SEED) {
            db.execSQL("INSERT INTO " + TABLE_KHOAHOC + " ("
                            + COL_TEN_KH + ", " + COL_NGAY_BD + ", "
                            + COL_NGAY_KT + ", " + COL_GIANG_VIEN + ") VALUES (?, ?, ?, ?)",
                    new Object[]{kh[0], kh[1], kh[2], kh[3]});
        }

        // 5. Seed Chi Tiết Đào Tạo
        for (String[] dt : DAO_TAO_SEED) {
            db.execSQL("INSERT INTO " + TABLE_CHITIET_DAOTAO + " ("
                            + COL_ID_KH_FK + ", " + COL_ID_NV_FK_DT + ", "
                            + COL_KET_QUA + ") VALUES (?, ?, ?)",
                    new Object[]{Integer.parseInt(dt[0]), Integer.parseInt(dt[1]), dt[2]});
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYLUAT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KHENTHUONG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUONG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHUYENCAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHANVIEN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONGBAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAIKHOAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KHOAHOC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHITIET_DAOTAO);
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}