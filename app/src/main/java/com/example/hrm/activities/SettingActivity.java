package com.example.hrm.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.hrm.R;
import com.example.hrm.dao.AccountDAO;
import com.example.hrm.utils.BackupService;
import com.example.hrm.utils.DateUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.concurrent.Executor;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat swBiometric, swDarkMode;
    private LinearLayout btnChangePassword, btnWorkShift, btnBackup, btnRestore, btnAbout, btnDateFormat, btnTimeFormat;
    private Button btnLogout;
    private SharedPreferences prefs;
    private TextView tvWorkShiftTime, tvDateFormat, tvTimeFormat;
    private static final int REQ_BACKUP_DB = 301;
    private static final int REQ_EXPORT_EXCEL = 302;
    private static final int REQ_RESTORE_DB = 303;
    private boolean isInternalChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("SESSION", MODE_PRIVATE);

        initViews();
        loadSettings();
        setupEvents();
        setupToolbar();
    }

    private void initViews() {
        swBiometric = findViewById(R.id.swBiometric);
        swDarkMode = findViewById(R.id.swDarkMode);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnWorkShift = findViewById(R.id.btnWorkShift);
        btnBackup = findViewById(R.id.btnBackup);
        btnRestore = findViewById(R.id.btnRestore);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        tvWorkShiftTime = findViewById(R.id.tvWorkShiftTime);
        btnDateFormat = findViewById(R.id.btnDateFormat);
        btnTimeFormat = findViewById(R.id.btnTimeFormat);
        tvDateFormat = findViewById(R.id.tvDateFormat);
        tvTimeFormat = findViewById(R.id.tvTimeFormat);
    }

    private void loadSettings() {
        isInternalChange = true;
        swBiometric.setChecked(prefs.getBoolean("isBiometricEnabled", false));
        swDarkMode.setChecked(prefs.getBoolean("isDarkMode", false));
        isInternalChange = false;
        tvWorkShiftTime.setText(prefs.getString("work_shift", "08:00"));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEvents() {
        swBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInternalChange) return;

            if (isChecked) {
                confirmBiometricToEnable();
            } else {
                prefs.edit().putBoolean("isBiometricEnabled", false).apply();
                Toast.makeText(this, "Đã tắt đăng nhập vân tay", Toast.LENGTH_SHORT).show();
            }
        });

        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInternalChange) return;

            // 1. Lưu preference trước
            prefs.edit().putBoolean("isDarkMode", isChecked).apply();

            // 2. Hiện Dialog chờ
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Đang thay đổi giao diện...");
            dialog.setCancelable(false);
            dialog.show();

            new Handler().postDelayed(() -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }, 500);
        });

        btnDateFormat.setOnClickListener(v -> showDateFormatDialog());
        btnTimeFormat.setOnClickListener(v -> showTimeFormatDialog());

        btnWorkShift.setOnClickListener(v -> {
            // Lấy giờ hiện tại từ prefs, mặc định là 08:00
            String currentTime = prefs.getString("work_shift", "08:00");
            String[] parts = currentTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                prefs.edit().putString("work_shift", time).apply();

                if (tvWorkShiftTime != null) tvWorkShiftTime.setText(time);
                Toast.makeText(this, "Đã cập nhật giờ vào làm!", Toast.LENGTH_SHORT).show();

            }, hour, minute, true).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        btnBackup.setOnClickListener(v -> {
            showBackupOptions();
        });
        btnRestore.setOnClickListener(v -> {
            showRestoreDialog();
        });

        // 3. Thông tin phiên bản
        btnAbout.setOnClickListener(v -> showAboutDialog());

        // 4. Đăng xuất
        btnLogout.setOnClickListener(v -> {
            prefs.edit().putBoolean("isLogin", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void confirmBiometricToEnable() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        prefs.edit().putBoolean("isBiometricEnabled", true).apply();
                        Toast.makeText(SettingActivity.this, "Đã kích hoạt vân tay!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        revertSwitch();
                        Toast.makeText(SettingActivity.this, "Xác nhận thất bại!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Xác thực vân tay")
                .setSubtitle("Quét vân tay để kích hoạt đăng nhập nhanh")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void revertSwitch() {
        isInternalChange = true;
        swBiometric.setChecked(false);
        isInternalChange = false;
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_about, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnClose = view.findViewById(R.id.btnCloseAbout);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDateFormatDialog() {
        String[] options = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"};

        String currentFormat = prefs.getString("date_format", DateUtils.DEFAULT_DATE_FORMAT);
        int checkedItem = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(currentFormat)) {
                checkedItem = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn định dạng ngày");
        builder.setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
            // Khi user bấm chọn cái nào, lưu ngay vào SharedPreferences
            String selected = options[which];
            prefs.edit().putString("date_format", selected).apply();

            Toast.makeText(this, "Đã đổi sang: " + selected, Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // Mày có thể reload lại UI ở đây nếu cần hiện mẫu ngày tháng mới
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showTimeFormatDialog() {
        String[] options = {"12 giờ (07:00 PM)", "24 giờ (19:00)"};
        String[] values = {"hh:mm a", "HH:mm"};
        String currentVal = prefs.getString("time_format", DateUtils.DEFAULT_TIME_FORMAT);

        int checkedItem = currentVal.equals("HH:mm") ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("Chọn định dạng giờ")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    String selectedVal = values[which];
                    prefs.edit().putString("time_format", selectedVal).apply();

                    if (tvTimeFormat != null) tvTimeFormat.setText(options[which]);

                    Toast.makeText(this, "Đã đổi định dạng giờ!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showBackupOptions() {
        String[] options = {"Xuất file Excel (.xlsx)", "Sao lưu Database (.db)"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn định dạng sao lưu")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openFilePicker("HRM_NhanVien.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", REQ_EXPORT_EXCEL);
                    } else {
                        openFilePicker("HRM_Backup.db", "application/octet-stream", REQ_BACKUP_DB);
                    }
                }).show();
    }

    private void openFilePicker(String fileName, String mimeType, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();

            try {
                if (requestCode == REQ_BACKUP_DB) {
                    com.example.hrm.utils.BackupService.backupDatabase(this, fileUri);
                    Toast.makeText(this, "Đã sao lưu Database!", Toast.LENGTH_SHORT).show();

                } else if (requestCode == REQ_EXPORT_EXCEL) {
                    com.example.hrm.utils.BackupService.exportFullDatabaseToExcel(this, fileUri);
                    Toast.makeText(this, "Đã xuất file Excel!", Toast.LENGTH_SHORT).show();

                } else if (requestCode == REQ_RESTORE_DB) {
                    handleRestore(fileUri);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void showRestoreDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận khôi phục")
                .setMessage("Toàn bộ dữ liệu hiện tại sẽ bị ghi đè. Bạn có chắc chắn không?")
                .setPositiveButton("Khôi phục ngay", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/octet-stream");
                    startActivityForResult(intent, REQ_RESTORE_DB);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleRestore(Uri uri) {
        try {
            com.example.hrm.utils.BackupService.restoreDatabase(this, uri);
            Toast.makeText(this, "Khôi phục thành công! Đang khởi động lại app...", Toast.LENGTH_LONG).show();

            // Restart app sau 2 giây để DB mới có hiệu lực
            new Handler().postDelayed(() -> {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                finish();
                System.exit(0);
            }, 2000);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khôi phục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        TextInputLayout tilOld = view.findViewById(R.id.tilOldPassword);
        TextInputLayout tilNew = view.findViewById(R.id.tilNewPassword);
        TextInputLayout tilConf = view.findViewById(R.id.tilConfirmPassword);

        EditText edtOldPass = view.findViewById(R.id.edtOldPassword);
        EditText edtNewPass = view.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPass = view.findViewById(R.id.edtConfirmPassword);
        Button btnSave = view.findViewById(R.id.btnSavePassword);
        Button btnCancel = view.findViewById(R.id.btnCancelPassword);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            tilOld.setError(null);
            tilNew.setError(null);
            tilConf.setError(null);

            String oldP = edtOldPass.getText().toString().trim();
            String newP = edtNewPass.getText().toString().trim();
            String confP = edtConfirmPass.getText().toString().trim();

            if (oldP.isEmpty()) {
                tilOld.setError("Nhập mật khẩu hiện tại");
                return;
            }
            if (newP.length() < 6) {
                tilNew.setError("Mật khẩu mới phải từ 6 ký tự");
                return;
            }
            if (!newP.equals(confP)) {
                tilConf.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            String currentUsername = prefs.getString("username", "");
            AccountDAO accountDAO = new AccountDAO(this);

            if (accountDAO.updatePassword(currentUsername, oldP, newP)) {
                Toast.makeText(this, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLogin", false);

                editor.putBoolean("isBiometricEnabled", false);

                editor.putString("username", "");
                editor.putString("adminname", "");
                editor.apply();

                dialog.dismiss();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                tilOld.setError("Mật khẩu cũ không chính xác!");
            }
        });
    }
}