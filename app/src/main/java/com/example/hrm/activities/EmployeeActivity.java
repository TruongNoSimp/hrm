package com.example.hrm.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.EmployeeAdapter;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.models.Department;
import com.example.hrm.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployee;
    private FloatingActionButton fabAddEmployee;
    private EditText edtSearchEmployee;

    private EmployeeDAO employeeDAO;
    private List<Employee> employeeList;
    private List<Employee> originalList;
    private EmployeeAdapter employeeAdapter;

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

        employeeAdapter = new EmployeeAdapter(employeeList, new EmployeeAdapter.OnEmployeeActionListener() {
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
                Toast.makeText(
                        EmployeeActivity.this,
                        "Nhân viên: " + employee.getHoTen(),
                        Toast.LENGTH_SHORT
                ).show();
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

    private void showEmployeeDialog(Employee employee, boolean isEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_employee, null);
        builder.setView(view);

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

        AlertDialog dialog = builder.create();
        dialog.show();

        List<Department> departmentList = employeeDAO.getAllDepartments();
        List<String> departmentNames = new ArrayList<>();
        for (Department d : departmentList) {
            departmentNames.add(d.getTenPhong());
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nam", "Nữ", "Khác"}
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGioiTinh.setAdapter(genderAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                departmentNames
        );
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhongBan.setAdapter(departmentAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Đang làm", "Đã nghỉ"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrangThai.setAdapter(statusAdapter);

        if (isEdit && employee != null) {
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
        }

        btnCloseEmployeeDialog.setOnClickListener(v -> dialog.dismiss());

        btnSaveEmployee.setOnClickListener(v -> {
            String maNv = edtMaNv.getText().toString().trim();
            String hoTen = edtHoTen.getText().toString().trim();
            String ngaySinh = edtNgaySinh.getText().toString().trim();
            String gioiTinh = spinnerGioiTinh.getSelectedItem().toString();
            String soDt = edtSoDt.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String chucVu = edtChucVu.getText().toString().trim();
            String ngayVaoLam = edtNgayVaoLam.getText().toString().trim();
            String heSoLuongStr = edtHeSoLuong.getText().toString().trim();
            int trangThai = spinnerTrangThai.getSelectedItemPosition() == 0 ? 1 : 0;

            if (TextUtils.isEmpty(maNv)) {
                edtMaNv.setError("Không được để trống mã nhân viên");
                return;
            }

            if (TextUtils.isEmpty(hoTen)) {
                edtHoTen.setError("Không được để trống họ tên");
                return;
            }

            if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                return;
            }

            if (TextUtils.isEmpty(heSoLuongStr)) {
                edtHeSoLuong.setError("Không được để trống hệ số lương");
                return;
            }

            double heSoLuong;
            try {
                heSoLuong = Double.parseDouble(heSoLuongStr);
            } catch (Exception e) {
                edtHeSoLuong.setError("Hệ số lương không hợp lệ");
                return;
            }

            int selectedDepartmentPosition = spinnerPhongBan.getSelectedItemPosition();
            int idPhongBan = departmentList.get(selectedDepartmentPosition).getIdPhongBan();

            if (isEdit && employee != null) {
                employee.setHoTen(hoTen);
                employee.setNgaySinh(ngaySinh);
                employee.setGioiTinh(gioiTinh);
                employee.setSoDt(soDt);
                employee.setEmail(email);
                employee.setIdPhongBan(idPhongBan);
                employee.setChucVu(chucVu);
                employee.setNgayVaoLam(ngayVaoLam);
                employee.setHeSoLuong(heSoLuong);
                employee.setTrangThai(trangThai);

                boolean result = employeeDAO.updateEmployee(employee);
                if (result) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Employee newEmployee = new Employee();
                newEmployee.setMaNv(maNv);
                newEmployee.setHoTen(hoTen);
                newEmployee.setNgaySinh(ngaySinh);
                newEmployee.setGioiTinh(gioiTinh);
                newEmployee.setSoDt(soDt);
                newEmployee.setEmail(email);
                newEmployee.setIdPhongBan(idPhongBan);
                newEmployee.setChucVu(chucVu);
                newEmployee.setNgayVaoLam(ngayVaoLam);
                newEmployee.setHeSoLuong(heSoLuong);
                newEmployee.setTrangThai(trangThai);

                boolean result = employeeDAO.insertEmployee(newEmployee);
                if (result) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Mã nhân viên đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
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
                .setTitle("Xóa nhân viên")
                .setMessage("Bạn có chắc muốn xóa không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean result = employeeDAO.deleteEmployee(employee.getIdNv());

                    if (result) {
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadEmployees();
                    } else {
                        Toast.makeText(this, "Không thể xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}