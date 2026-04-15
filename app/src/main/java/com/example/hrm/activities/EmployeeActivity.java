package com.example.hrm.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.EmployeeAdapter;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.listeners.OnEmployeeActionListener;
import com.example.hrm.models.Department;
import com.example.hrm.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EmployeeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployee;
    private FloatingActionButton fabAddEmployee;
    private EditText edtSearchEmployee;
    private EmployeeDAO employeeDAO;
    private List<Employee> employeeList;
    private List<Employee> originalList;
    private EmployeeAdapter employeeAdapter;
    private String currentAvatarUri = "";
    private ImageView imgDialogAvatar;
    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        initViews();
        initData();
        loadEmployees();
        initSearch();
        setupToolbar();
    }

    private void initViews() {
        recyclerViewEmployee = findViewById(R.id.recyclerViewEmployee);
        fabAddEmployee = findViewById(R.id.fabAddEmployee);
        edtSearchEmployee = findViewById(R.id.edtSearchEmployee);
    }

    private void initData() {
        employeeDAO = new EmployeeDAO(this);
        employeeList = new ArrayList<>();
        originalList = new ArrayList<>();

        recyclerViewEmployee.setLayoutManager(new LinearLayoutManager(this));

        employeeAdapter = new EmployeeAdapter(employeeList, new OnEmployeeActionListener() {
            @Override
            public void onEdit(Employee employee) {
                showEmployeeDialog(employee, true);
            }

            @Override
            public void onDelete(Employee employee) {
                confirmDeleteEmployee(employee);
            }

            @Override
            public void onItemClick(Employee employee) {
                Toast.makeText(EmployeeActivity.this, "Nhân viên: " + employee.getHoTen(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewEmployee.setAdapter(employeeAdapter);
        fabAddEmployee.setOnClickListener(v -> showEmployeeDialog(null, false));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarEmployee);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadEmployees() {
        employeeList.clear();
        originalList.clear();
        List<Employee> data = employeeDAO.getAllEmployees();
        employeeList.addAll(data);
        originalList.addAll(data);
        employeeAdapter.notifyDataSetChanged();
    }

    private void initSearch() {
        edtSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployees(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterEmployees(String keyword) {
        employeeList.clear();
        if (keyword.isEmpty()) {
            employeeList.addAll(originalList);
        } else {
            for (Employee e : originalList) {
                if (e.getHoTen().toLowerCase().contains(keyword.toLowerCase())
                        || e.getMaNv().toLowerCase().contains(keyword.toLowerCase())
                        || (e.getTenPhongBan() != null && e.getTenPhongBan().toLowerCase().contains(keyword.toLowerCase()))) {
                    employeeList.add(e);
                }
            }
        }
        employeeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                android.util.Log.e("EmployeeActivity", "Error taking persistable URI permission", e);
            }

            currentAvatarUri = imageUri.toString();
            if (imgDialogAvatar != null) {
                imgDialogAvatar.setImageURI(imageUri);
            }
        }
    }

    private void showEmployeeDialog(Employee employee, boolean isEdit) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_employee, null);
            builder.setView(view);

            imgDialogAvatar = view.findViewById(R.id.imgDialogAvatar);
            EditText edtMaNv = view.findViewById(R.id.edtMaNv);
            EditText edtHoTen = view.findViewById(R.id.edtHoTen);
            EditText edtNgaySinh = view.findViewById(R.id.edtNgaySinh);
            Spinner spinnerGioiTinh = view.findViewById(R.id.spinnerGioiTinh);
            EditText edtSoDt = view.findViewById(R.id.edtSoDt);
            EditText edtEmail = view.findViewById(R.id.edtEmail);
            Spinner spinnerPhongBan = view.findViewById(R.id.spinnerPhongBan);
            EditText edtChucVu = view.findViewById(R.id.edtChucVu);
            EditText edtNgayVaoLam = view.findViewById(R.id.edtNgayVaoLam);
            EditText edtHeSoLuong = view.findViewById(R.id.edtHeSoLuong);
            Spinner spinnerTrangThai = view.findViewById(R.id.spinnerTrangThai);
            Button btnSaveEmployee = view.findViewById(R.id.btnSaveEmployee);
            Button btnCloseEmployeeDialog = view.findViewById(R.id.btnCloseEmployeeDialog);
            ImageView btnPlusHeSo = view.findViewById(R.id.btnPlusHeSo);
            ImageView btnMinusHeSo = view.findViewById(R.id.btnMinusHeSo);

            AlertDialog dialog = builder.create();
            dialog.show();

            setupDialogActions(dialog, edtNgaySinh, edtNgayVaoLam, edtHeSoLuong, btnPlusHeSo, btnMinusHeSo, btnCloseEmployeeDialog);

            List<Department> departmentList = employeeDAO.getAllDepartments();
            setupSpinners(spinnerGioiTinh, spinnerPhongBan, spinnerTrangThai, departmentList);

            if (isEdit && employee != null) {
                fillEmployeeData(employee, edtMaNv, edtHoTen, edtNgaySinh, edtSoDt, edtEmail, edtChucVu, edtNgayVaoLam, edtHeSoLuong, spinnerGioiTinh, spinnerPhongBan, spinnerTrangThai, departmentList);
            } else {
                currentAvatarUri = "";
            }
            btnSaveEmployee.setOnClickListener(v -> handleSaveEmployee(dialog, employee, isEdit, edtMaNv, edtHoTen, edtNgaySinh, spinnerGioiTinh, edtSoDt, edtEmail, spinnerPhongBan, departmentList, edtChucVu, edtNgayVaoLam, edtHeSoLuong, spinnerTrangThai));

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupDialogActions(AlertDialog dialog, EditText ns, EditText nvl, EditText hsl, ImageView plus, ImageView minus, Button close) {
        imgDialogAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        ns.setOnClickListener(v -> showDatePicker(ns));
        nvl.setOnClickListener(v -> showDatePicker(nvl));
        close.setOnClickListener(v -> dialog.dismiss());

        plus.setOnClickListener(v -> {
            try {
                double current = Double.parseDouble(hsl.getText().toString());
                hsl.setText(String.format(Locale.US, "%.2f", current + 0.1));
            } catch (Exception e) { hsl.setText("1.0"); }
        });

        minus.setOnClickListener(v -> {
            try {
                double current = Double.parseDouble(hsl.getText().toString());
                if (current > 0.1) hsl.setText(String.format(Locale.US, "%.2f", current - 0.1));
            } catch (Exception e) { hsl.setText("1.0"); }
        });
    }

    private void setupSpinners(Spinner gt, Spinner pb, Spinner tt, List<Department> departmentList) {
        List<String> departmentNames = new ArrayList<>();
        for (Department d : departmentList) { departmentNames.add(d.getTenPhong()); }

        ArrayAdapter<String> adapterGT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Nam", "Nữ", "Khác"});
        adapterGT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gt.setAdapter(adapterGT);

        ArrayAdapter<String> adapterPB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
        adapterPB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pb.setAdapter(adapterPB);

        ArrayAdapter<String> adapterTT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Đang làm", "Đã nghỉ"});
        adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tt.setAdapter(adapterTT);
    }

    private void fillEmployeeData(Employee e, EditText ma, EditText ten, EditText ns, EditText dt, EditText em, EditText cv, EditText nvl, EditText hsl, Spinner gt, Spinner pb, Spinner tt, List<Department> departmentList) {
        currentAvatarUri = e.getAvatar() != null ? e.getAvatar() : "";
        if (!currentAvatarUri.isEmpty()) imgDialogAvatar.setImageURI(Uri.parse(currentAvatarUri));

        ma.setText(e.getMaNv());
        ten.setText(e.getHoTen());
        ns.setText(e.getNgaySinh());
        dt.setText(e.getSoDt());
        em.setText(e.getEmail());
        cv.setText(e.getChucVu());
        nvl.setText(e.getNgayVaoLam());
        hsl.setText(String.valueOf(e.getHeSoLuong()));
        ma.setEnabled(false);

        setSpinnerSelection(gt, e.getGioiTinh());
        setDepartmentSelection(pb, departmentList, e.getIdPhongBan());
        tt.setSelection(e.getTrangThai() == 1 ? 0 : 1);
    }

    private void handleSaveEmployee(AlertDialog dialog, Employee employee, boolean isEdit, EditText edtMaNv, EditText edtHoTen, EditText ns, Spinner gt, EditText dt, EditText em, Spinner pb, List<Department> departmentList, EditText cv, EditText nvl, EditText hsl, Spinner tt) {
        String maNv = edtMaNv.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String chucVu = cv.getText().toString().trim();

        if (TextUtils.isEmpty(maNv)) { edtMaNv.setError("Cần mã NV"); return; }
        if (TextUtils.isEmpty(hoTen)) { edtHoTen.setError("Cần họ tên"); return; }
        if (TextUtils.isEmpty(chucVu)) { cv.setError("Cần chức vụ"); return; }

        double heSoLuong;
        try { heSoLuong = Double.parseDouble(hsl.getText().toString()); } catch (Exception e) { heSoLuong = 1.0; }

        int idPhongBan = departmentList.get(pb.getSelectedItemPosition()).getIdPhongBan();
        int trangThai = tt.getSelectedItemPosition() == 0 ? 1 : 0;

        Employee e = isEdit ? employee : new Employee();
        if (!isEdit) e.setMaNv(maNv);

        e.setHoTen(hoTen);
        e.setChucVu(chucVu);
        e.setNgaySinh(ns.getText().toString());
        e.setGioiTinh(gt.getSelectedItem().toString());
        e.setSoDt(dt.getText().toString());
        e.setEmail(em.getText().toString());
        e.setIdPhongBan(idPhongBan);
        e.setNgayVaoLam(nvl.getText().toString());
        e.setHeSoLuong(heSoLuong);
        e.setTrangThai(trangThai);
        e.setAvatar(currentAvatarUri);

        boolean success = isEdit ? employeeDAO.updateEmployee(e) : employeeDAO.insertEmployee(e);
        if (success) {
            loadEmployees();
            dialog.dismiss();
            Toast.makeText(this, isEdit ? "Đã cập nhật" : "Đã thêm", Toast.LENGTH_SHORT).show();
        } else if (!isEdit) {
            Toast.makeText(this, "Mã trùng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setDepartmentSelection(Spinner spinner, List<Department> departments, int idPhongBan) {
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getIdPhongBan() == idPhongBan) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void confirmDeleteEmployee(Employee employee) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa?")
                .setMessage("Bạn có chắc chắn xóa nhân viên này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (employeeDAO.deleteEmployee(employee.getIdNv())) {
                        loadEmployees();
                    }
                })
                .setNegativeButton("Hủy", null).show();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        if (!editText.getText().toString().isEmpty()) {
            try {
                String[] parts = editText.getText().toString().split("-");
                if (parts.length == 3) {
                    calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                }
            } catch (Exception e) {
                android.util.Log.e("EmployeeActivity", "Error parsing date", e);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}