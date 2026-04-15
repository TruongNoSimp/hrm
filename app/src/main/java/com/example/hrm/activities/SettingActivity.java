package com.example.hrm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.hrm.R;

import java.util.concurrent.Executor;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat swBiometric, swDarkMode;
    private LinearLayout btnChangePassword, btnWorkShift, btnBackup, btnRestore, btnAbout;
    private Button btnLogout;
    private SharedPreferences prefs;

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
    }

    private void loadSettings() {
        isInternalChange = true;
        swBiometric.setChecked(prefs.getBoolean("isBiometricEnabled", false));
        swDarkMode.setChecked(prefs.getBoolean("isDarkMode", false));
        isInternalChange = false;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEvents() {
        swBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInternalChange) return; // Code tự gạt thì không chạy tiếp

            if (isChecked) {
                confirmBiometricToEnable();
            } else {
                prefs.edit().putBoolean("isBiometricEnabled", false).apply();
                Toast.makeText(this, "Đã tắt đăng nhập vân tay", Toast.LENGTH_SHORT).show();
            }
        });

        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInternalChange) return;

            prefs.edit().putBoolean("isDarkMode", isChecked).apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            Toast.makeText(this, isChecked ? "Đã sang chế độ cày đêm!" : "Đã về chế độ ban ngày!", Toast.LENGTH_SHORT).show();
        });

        // 2. Sao lưu dữ liệu (G gọi hàm tao cho lúc nãy)
        btnBackup.setOnClickListener(v -> {
            Toast.makeText(this, "Đang sao lưu dữ liệu hệ thống...", Toast.LENGTH_SHORT).show();
            // backupDatabase(); // Gọi hàm backup vào đây
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

        // Mấy cái còn lại mày tự viết Toast "Chức năng đang phát triển" vào cho đỡ trống
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
}