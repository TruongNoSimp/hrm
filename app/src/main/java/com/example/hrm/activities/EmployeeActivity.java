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
import com.example.hrm.adapters.EmployeeInfoDialog;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.listeners.OnEmployeeActionListener;
import com.example.hrm.models.Department;
import com.example.hrm.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    ImageView imgEditEmployee, imgDeleteEmployee;
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

        employeeAdapter = new EmployeeAdapter(this, employeeList, new OnEmployeeActionListener() {
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
                showEmployeeDetail(employee);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployees(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

            imgDialogAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });

            edtNgaySinh.setOnClickListener(v -> showDatePicker(edtNgaySinh));
            edtNgayVaoLam.setOnClickListener(v -> showDatePicker(edtNgayVaoLam));

            List<Department> departmentList = employeeDAO.getAllDepartments();
            List<String> departmentNames = new ArrayList<>();
            for (Department d : departmentList) {
                departmentNames.add(d.getTenPhong());
            }

            ArrayAdapter<String> adapterGT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Nam", "Nữ", "Khác"});
            adapterGT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGioiTinh.setAdapter(adapterGT);

            ArrayAdapter<String> adapterPB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
            adapterPB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPhongBan.setAdapter(adapterPB);

            ArrayAdapter<String> adapterTT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Đang làm", "Đã nghỉ"});
            adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTrangThai.setAdapter(adapterTT);

            btnPlusHeSo.setOnClickListener(v -> {
                try {
                    double current = Double.parseDouble(edtHeSoLuong.getText().toString());
                    edtHeSoLuong.setText(String.format(java.util.Locale.US, "%.1f", current + 0.5));
                } catch (Exception e) {
                    edtHeSoLuong.setText("1.0");
                }
            });

            btnMinusHeSo.setOnClickListener(v -> {
                try {
                    double current = Double.parseDouble(edtHeSoLuong.getText().toString());
                    if (current > 0.1) {
                        edtHeSoLuong.setText(String.format(java.util.Locale.US, "%.1f", current - 0.5));
                    }
                } catch (Exception e) {
                    edtHeSoLuong.setText("1.0");
                }
            });

            if (isEdit && employee != null) {
                currentAvatarUri = employee.getAvatar() != null ? employee.getAvatar() : "";
                if (!currentAvatarUri.isEmpty()) {
                    imgDialogAvatar.setImageURI(Uri.parse(currentAvatarUri));
                }
                edtMaNv.setText(employee.getMaNv());
                edtHoTen.setText(employee.getHoTen());
                edtNgaySinh.setText(employee.getNgaySinh());
                edtSoDt.setText(employee.getSoDt());
                edtEmail.setText(employee.getEmail());
                edtChucVu.setText(employee.getChucVu());
                edtNgayVaoLam.setText(employee.getNgayVaoLam());
                edtHeSoLuong.setText(String.valueOf(employee.getHeSoLuong()));
                edtMaNv.setEnabled(false);

                setSpinnerSelection(spinnerGioiTinh, employee.getGioiTinh());
                setDepartmentSelection(spinnerPhongBan, departmentList, employee.getIdPhongBan());
                spinnerTrangThai.setSelection(employee.getTrangThai() == 1 ? 0 : 1);
            } else {
                currentAvatarUri = "";
            }

            btnCloseEmployeeDialog.setOnClickListener(v -> dialog.dismiss());

            btnSaveEmployee.setOnClickListener(v -> {
                String maNv = edtMaNv.getText().toString().trim();
                String hoTen = edtHoTen.getText().toString().trim();
                String chucVu = edtChucVu.getText().toString().trim();
                String heSoLuongStr = edtHeSoLuong.getText().toString().trim();

                if (TextUtils.isEmpty(maNv)) {
                    edtMaNv.setError("Cần mã NV");
                    return;
                }
                if (TextUtils.isEmpty(hoTen)) {
                    edtHoTen.setError("Cần họ tên");
                    return;
                }
                if (TextUtils.isEmpty(chucVu)) {
                    edtChucVu.setError("Cần chức vụ");
                    return;
                }

                double heSoLuong;
                try {
                    heSoLuong = Double.parseDouble(heSoLuongStr);
                } catch (Exception e) {
                    heSoLuong = 0;
                }

                int idPhongBan = departmentList.get(spinnerPhongBan.getSelectedItemPosition()).getIdPhongBan();
                int trangThai = spinnerTrangThai.getSelectedItemPosition() == 0 ? 1 : 0;

                if (isEdit && employee != null) {
                    employee.setHoTen(hoTen);
                    employee.setChucVu(chucVu);
                    employee.setNgaySinh(edtNgaySinh.getText().toString());
                    employee.setGioiTinh(spinnerGioiTinh.getSelectedItem().toString());
                    employee.setSoDt(edtSoDt.getText().toString());
                    employee.setEmail(edtEmail.getText().toString());
                    employee.setIdPhongBan(idPhongBan);
                    employee.setNgayVaoLam(edtNgayVaoLam.getText().toString());
                    employee.setHeSoLuong(heSoLuong);
                    employee.setTrangThai(trangThai);
                    employee.setAvatar(currentAvatarUri);

                    if (employeeDAO.updateEmployee(employee)) {
                        loadEmployees();
                        dialog.dismiss();
                        Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Employee newEmployee = new Employee();
                    newEmployee.setMaNv(maNv);
                    newEmployee.setHoTen(hoTen);
                    newEmployee.setChucVu(chucVu);
                    newEmployee.setNgaySinh(edtNgaySinh.getText().toString());
                    newEmployee.setGioiTinh(spinnerGioiTinh.getSelectedItem().toString());
                    newEmployee.setSoDt(edtSoDt.getText().toString());
                    newEmployee.setEmail(edtEmail.getText().toString());
                    newEmployee.setIdPhongBan(idPhongBan);
                    newEmployee.setNgayVaoLam(edtNgayVaoLam.getText().toString());
                    newEmployee.setHeSoLuong(heSoLuong);
                    newEmployee.setTrangThai(trangThai);
                    newEmployee.setAvatar(currentAvatarUri);

                    if (employeeDAO.insertEmployee(newEmployee)) {
                        loadEmployees();
                        dialog.dismiss();
                        Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Mã trùng!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("EmployeeActivity", "Error in showEmployeeDialog", e);
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
                .setMessage("Ban có chắc chắn xóa nhân viên này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (employeeDAO.deleteEmployee(employee.getIdNv())) {
                        loadEmployees();
                    }
                })
                .setNegativeButton("Hủy", null).show();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();

        // Nếu đã có ngày được chọn, parse nó để set initial date
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

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(java.util.Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showEmployeeDetail(Employee employee) {
        try {
            EmployeeInfoDialog dialog = new EmployeeInfoDialog(this, employee);
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Không thể hiển thị chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}