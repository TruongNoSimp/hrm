package com.example.hrm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.example.hrm.models.Department;
import com.example.hrm.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import com.example.hrm.listeners.OnItemActionListener;

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

        employeeAdapter = new EmployeeAdapter(employeeList, new OnItemActionListener<Employee>() {
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

            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            currentAvatarUri = imageUri.toString();
            if (imgDialogAvatar != null) {
                imgDialogAvatar.setImageURI(imageUri);
            }
        }
    }

    private void showEmployeeDialog(Employee employee, boolean isEdit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_employee, null);
        AlertDialog dialog = createEmployeeDialog(view);

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

        List<Department> departmentList = getDepartmentList();
        setupAvatarClick();
        setupGenderSpinner(spinnerGioiTinh);
        setupDepartmentSpinner(spinnerPhongBan, departmentList);
        setupStatusSpinner(spinnerTrangThai);
        bindEmployeeData(employee, isEdit, departmentList, edtMaNv, edtHoTen, edtNgaySinh,
                edtSoDt, edtEmail, edtChucVu, edtNgayVaoLam, edtHeSoLuong,
                spinnerGioiTinh, spinnerPhongBan, spinnerTrangThai);
        setupCloseButton(dialog, btnCloseEmployeeDialog);
        setupSaveEmployeeButton(dialog, employee, isEdit, departmentList,
                edtMaNv, edtHoTen, edtNgaySinh, edtSoDt, edtEmail, edtChucVu,
                edtNgayVaoLam, edtHeSoLuong, spinnerGioiTinh, spinnerPhongBan,
                spinnerTrangThai, btnSaveEmployee);
    }

    private AlertDialog createEmployeeDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private List<Department> getDepartmentList() {
        List<Department> list = employeeDAO.getAllDepartments();
        return list != null ? list : new ArrayList<>();
    }

    private void setupAvatarClick() {
        imgDialogAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void setupGenderSpinner(Spinner spinnerGioiTinh) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nam", "Nữ", "Khác"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGioiTinh.setAdapter(adapter);
    }

    private void setupDepartmentSpinner(Spinner spinnerPhongBan, List<Department> departmentList) {
        List<String> departmentNames = new ArrayList<>();
        for (Department d : departmentList) {
            departmentNames.add(d.getTenPhong());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                departmentNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhongBan.setAdapter(adapter);
    }

    private void setupStatusSpinner(Spinner spinnerTrangThai) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Đang làm", "Đã nghỉ"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrangThai.setAdapter(adapter);
    }

    private void bindEmployeeData(Employee employee, boolean isEdit,
                                  List<Department> departmentList,
                                  EditText edtMaNv, EditText edtHoTen, EditText edtNgaySinh,
                                  EditText edtSoDt, EditText edtEmail, EditText edtChucVu,
                                  EditText edtNgayVaoLam, EditText edtHeSoLuong,
                                  Spinner spinnerGioiTinh, Spinner spinnerPhongBan,
                                  Spinner spinnerTrangThai) {
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
    }

    private void setupCloseButton(AlertDialog dialog, Button btnClose) {
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupSaveEmployeeButton(AlertDialog dialog, Employee employee, boolean isEdit,
                                         List<Department> departmentList,
                                         EditText edtMaNv, EditText edtHoTen, EditText edtNgaySinh,
                                         EditText edtSoDt, EditText edtEmail, EditText edtChucVu,
                                         EditText edtNgayVaoLam, EditText edtHeSoLuong,
                                         Spinner spinnerGioiTinh, Spinner spinnerPhongBan,
                                         Spinner spinnerTrangThai, Button btnSaveEmployee) {
        btnSaveEmployee.setOnClickListener(v -> {
            EmployeeInput input = getEmployeeInput(
                    edtMaNv, edtHoTen, edtNgaySinh, edtSoDt, edtEmail,
                    edtChucVu, edtNgayVaoLam, edtHeSoLuong,
                    spinnerGioiTinh, spinnerPhongBan, spinnerTrangThai,
                    departmentList
            );

            if (!validateEmployeeInput(input, edtMaNv, edtHoTen, edtChucVu)) {
                return;
            }

            if (isEdit && employee != null) {
                updateEmployee(dialog, employee, input);
            } else {
                insertEmployee(dialog, input);
            }
        });
    }

    private EmployeeInput getEmployeeInput(EditText edtMaNv, EditText edtHoTen, EditText edtNgaySinh,
                                           EditText edtSoDt, EditText edtEmail, EditText edtChucVu,
                                           EditText edtNgayVaoLam, EditText edtHeSoLuong,
                                           Spinner spinnerGioiTinh, Spinner spinnerPhongBan,
                                           Spinner spinnerTrangThai, List<Department> departmentList) {
        EmployeeInput input = new EmployeeInput();
        input.maNv = edtMaNv.getText().toString().trim();
        input.hoTen = edtHoTen.getText().toString().trim();
        input.ngaySinh = edtNgaySinh.getText().toString().trim();
        input.soDt = edtSoDt.getText().toString().trim();
        input.email = edtEmail.getText().toString().trim();
        input.chucVu = edtChucVu.getText().toString().trim();
        input.ngayVaoLam = edtNgayVaoLam.getText().toString().trim();
        input.heSoLuong = parseHeSoLuong(edtHeSoLuong.getText().toString().trim());
        input.gioiTinh = spinnerGioiTinh.getSelectedItem().toString();
        input.idPhongBan = departmentList.get(spinnerPhongBan.getSelectedItemPosition()).getIdPhongBan();
        input.trangThai = spinnerTrangThai.getSelectedItemPosition() == 0 ? 1 : 0;
        input.avatar = currentAvatarUri;
        return input;
    }

    private boolean validateEmployeeInput(EmployeeInput input,
                                          EditText edtMaNv,
                                          EditText edtHoTen,
                                          EditText edtChucVu) {
        if (TextUtils.isEmpty(input.maNv)) {
            edtMaNv.setError("Cần mã NV");
            return false;
        }

        if (TextUtils.isEmpty(input.hoTen)) {
            edtHoTen.setError("Cần họ tên");
            return false;
        }

        if (TextUtils.isEmpty(input.chucVu)) {
            edtChucVu.setError("Cần chức vụ");
            return false;
        }

        return true;
    }

    private double parseHeSoLuong(String heSoLuongStr) {
        try {
            return Double.parseDouble(heSoLuongStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateEmployee(AlertDialog dialog, Employee employee, EmployeeInput input) {
        employee.setHoTen(input.hoTen);
        employee.setChucVu(input.chucVu);
        employee.setNgaySinh(input.ngaySinh);
        employee.setGioiTinh(input.gioiTinh);
        employee.setSoDt(input.soDt);
        employee.setEmail(input.email);
        employee.setIdPhongBan(input.idPhongBan);
        employee.setNgayVaoLam(input.ngayVaoLam);
        employee.setHeSoLuong(input.heSoLuong);
        employee.setTrangThai(input.trangThai);
        employee.setAvatar(input.avatar);

        if (employeeDAO.updateEmployee(employee)) {
            loadEmployees();
            dialog.dismiss();
            Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertEmployee(AlertDialog dialog, EmployeeInput input) {
        Employee newEmployee = new Employee();
        newEmployee.setMaNv(input.maNv);
        newEmployee.setHoTen(input.hoTen);
        newEmployee.setChucVu(input.chucVu);
        newEmployee.setNgaySinh(input.ngaySinh);
        newEmployee.setGioiTinh(input.gioiTinh);
        newEmployee.setSoDt(input.soDt);
        newEmployee.setEmail(input.email);
        newEmployee.setIdPhongBan(input.idPhongBan);
        newEmployee.setNgayVaoLam(input.ngayVaoLam);
        newEmployee.setHeSoLuong(input.heSoLuong);
        newEmployee.setTrangThai(input.trangThai);
        newEmployee.setAvatar(input.avatar);

        if (employeeDAO.insertEmployee(newEmployee)) {
            loadEmployees();
            dialog.dismiss();
            Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mã trùng!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class EmployeeInput {
        String maNv;
        String hoTen;
        String ngaySinh;
        String gioiTinh;
        String soDt;
        String email;
        String chucVu;
        String ngayVaoLam;
        double heSoLuong;
        int idPhongBan;
        int trangThai;
        String avatar;
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
                .setTitle("Xóa?")
                .setMessage("Chắc chắn không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (employeeDAO.deleteEmployee(employee.getIdNv())) {
                        loadEmployees();
                    }
                })
                .setNegativeButton("Hủy", null).show();
    }
}