package com.example.hrm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.hrm.R;
import com.example.hrm.dao.AccountDAO;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private CheckBox chkRemember;
    private AccountDAO accountDAO;

    private ImageView imgFingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
        boolean isLogin = prefs.getBoolean("isLogin", false);
        boolean isRemember = prefs.getBoolean("remember", false);
        boolean isBiometricEnabled = prefs.getBoolean("isBiometricEnabled", false);

        setContentView(R.layout.activity_login);
        initViews();

        if (isBiometricEnabled) {
            imgFingerprint.setVisibility(android.view.View.VISIBLE);
        } else {
            imgFingerprint.setVisibility(android.view.View.GONE);
        }

        if (isRemember && isBiometricEnabled) {
            checkBiometric();
        }

        btnLogin.setOnClickListener(v -> handleLogin());

        imgFingerprint.setOnClickListener(v -> {
            if (isBiometricEnabled) {
                checkBiometric();
            } else {
                Toast.makeText(this, "Mày chưa bật vân tay trong Cài đặt!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        chkRemember = findViewById(R.id.chkRemember);
        imgFingerprint = findViewById(R.id.imgFingerprint);
        accountDAO = new AccountDAO(this);
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        boolean isLogin = accountDAO.checkLogin(username, password);

        if (isLogin) {
            SharedPreferences.Editor editor =
                    getSharedPreferences("SESSION", MODE_PRIVATE).edit();

            editor.putBoolean("isLogin", true);
            editor.putBoolean("remember", chkRemember.isChecked());
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Rất tiếc! Thiết bị không hỗ trợ chức năng này", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Vân tay chưa được thiết lập", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập nhanh")
                .setSubtitle("Dùng vân tay để vào hệ thống HRM")
                .setNegativeButtonText("Nhập mật khẩu thủ công")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}