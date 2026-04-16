package com.example.hrm.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.database.DBHelper;
import com.example.hrm.models.Employee;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.List;

public class BackupService {
    public static void backupDatabase(Context context, Uri targetUri) throws IOException {
        File dbFile = context.getDatabasePath("hrm.db");

        try (InputStream in = new FileInputStream(dbFile); OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    public static void exportFullDatabaseToExcel(Context context, Uri targetUri) throws IOException {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'android_%' AND name NOT LIKE 'sqlite_%'", null);

        try (Workbook workbook = new XSSFWorkbook(); OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {

            if (tableCursor.moveToFirst()) {
                do {
                    String tableName = tableCursor.getString(0);
                    Sheet sheet = workbook.createSheet(tableName);

                    Cursor dataCursor = db.rawQuery("SELECT * FROM " + tableName, null);

                    if (dataCursor != null) {
                        Row headerRow = sheet.createRow(0);
                        String[] columnNames = dataCursor.getColumnNames();
                        for (int i = 0; i < columnNames.length; i++) {
                            headerRow.createCell(i).setCellValue(columnNames[i]);
                        }
                        int rowIdx = 1;
                        while (dataCursor.moveToNext()) {
                            Row row = sheet.createRow(rowIdx++);
                            for (int i = 0; i < columnNames.length; i++) {
                                String val = dataCursor.getString(i);
                                row.createCell(i).setCellValue(val != null ? val : "");
                            }
                        }
                        dataCursor.close();
                    }
                } while (tableCursor.moveToNext());
            }
            tableCursor.close();

            workbook.write(out);
            out.flush();
        } finally {
            db.close();
        }
    }

    public static void restoreDatabase(Context context, Uri sourceUri) throws IOException {
        File dbFile = context.getDatabasePath("hrm.db");

        try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
             OutputStream out = new FileOutputStream(dbFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
        }
    }
}