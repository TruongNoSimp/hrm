package com.example.hrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hrm.R;
import com.example.hrm.models.Employee;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EmployeeInfoDialog extends BottomSheetDialog {

    private TextView tvHoTen, tvMaNV, tvStatus, tvSDT, tvEmail, tvNgaySinh, tvGioiTinh;
    private TextView tvPhongBan, tvChucVu, tvNgayVao, tvHeSo;
    private ImageView imgAvatar;
    private Employee employee;

    public EmployeeInfoDialog(@NonNull Context context, Employee employee) {
        super(context);
        this.employee = employee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_employee);

        initViews();
        initActions();
        displayEmployeeData();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgDetailAvatar);
        tvHoTen = findViewById(R.id.tvDetailHoTen);
        tvMaNV = findViewById(R.id.tvDetailMaNV);
        tvStatus = findViewById(R.id.tvDetailStatus);

        tvSDT = findViewById(R.id.tvDetailSDT);
        tvEmail = findViewById(R.id.tvDetailEmail);
        tvNgaySinh = findViewById(R.id.tvDetailNgaySinh);
        tvGioiTinh = findViewById(R.id.tvDetailGioiTinh);

        tvPhongBan = findViewById(R.id.tvDetailPhongBan);
        tvChucVu = findViewById(R.id.tvDetailChucVu);
        tvNgayVao = findViewById(R.id.tvDetailNgayVao);
        tvHeSo = findViewById(R.id.tvDetailHeSo);
    }

    public void initActions() {
        tvSDT.setOnClickListener(v -> {
            String phone = employee.getSoDt();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                getContext().startActivity(intent);
            }
        });

        tvEmail.setOnClickListener(v -> {
            String email = employee.getEmail();
            if (email != null && !email.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ nhân viên");
                getContext().startActivity(Intent.createChooser(intent, "Gửi email bằng..."));
            }
        });
    }

    private void displayEmployeeData() {
        if (employee == null || tvHoTen == null) return;

        tvHoTen.setText(employee.getHoTen() != null ? employee.getHoTen().toUpperCase() : "");
        tvMaNV.setText("Mã NV: " + (employee.getMaNv() != null ? employee.getMaNv() : "---"));
        tvSDT.setText(employee.getSoDt() != null ? employee.getSoDt() : "---");
        tvEmail.setText(employee.getEmail() != null ? employee.getEmail() : "---");
        tvPhongBan.setText(employee.getTenPhongBan());
        tvChucVu.setText(employee.getChucVu());
        tvNgayVao.setText(employee.getNgayVaoLam());
        tvHeSo.setText(String.valueOf(employee.getHeSoLuong()));
        tvNgaySinh.setText(employee.getNgaySinh());
        tvGioiTinh.setText(employee.getGioiTinh());

        if (employee.getAvatar() != null && !employee.getAvatar().isEmpty()) {
            try {
                imgAvatar.setImageURI(Uri.parse(employee.getAvatar()));
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar_default);
        }

        if (employee.getTrangThai() == 1) {
            tvStatus.setText("Đang làm việc");
            tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else {
            tvStatus.setText("Đã nghỉ việc");
            tvStatus.setBackgroundResource(R.drawable.bg_status_red);
        }
    }
}