package com.example.hrm.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.DepartmentAdapter;
import com.example.hrm.dao.DepartmentDAO;
import com.example.hrm.listeners.OnItemActionListener;
import com.example.hrm.models.Department;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DepartmentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDepartment;
    private FloatingActionButton fabAddDepartment;
    private EditText edtSearchDepartment;

    private DepartmentDAO departmentDAO;
    private List<Department> departmentList;
    private List<Department> originalList;
    private DepartmentAdapter departmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        initViews();
        initData();
        loadDepartments();
        initSearch();
        setupToolbar();
    }

    private void initViews() {
        recyclerViewDepartment = findViewById(R.id.recyclerViewDepartment);
        fabAddDepartment = findViewById(R.id.fabAddDepartment);
        edtSearchDepartment = findViewById(R.id.edtSearchDepartment);
    }

    private void initData() {
        departmentDAO = new DepartmentDAO(this);
        departmentList = new ArrayList<>();
        originalList = new ArrayList<>();

        recyclerViewDepartment.setLayoutManager(new LinearLayoutManager(this));

        departmentAdapter = new DepartmentAdapter(departmentList, new OnItemActionListener<Department>() {
            @Override
            public void onEdit(Department department) {
                showDepartmentDialog(department, true);
            }

            @Override
            public void onDelete(Department department) {
                confirmDeleteDepartment(department);
            }

            @Override
            public void onItemClick(Department department) {
                // chưa dùng thì để trống
            }
        });

        recyclerViewDepartment.setAdapter(departmentAdapter);

        fabAddDepartment.setOnClickListener(v -> showDepartmentDialog(null, false));
    }

    private void loadDepartments() {
        departmentList.clear();
        originalList.clear();
        List<Department> data = departmentDAO.getAllDepartments();
        departmentList.addAll(data);
        originalList.addAll(data);

        departmentAdapter.notifyDataSetChanged();
    }


    private void initSearch() {
        edtSearchDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDepartments(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDepartment);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void filterDepartments(String keyword) {
        departmentList.clear();

        if (keyword.isEmpty()) {
            departmentList.addAll(originalList);
        } else {
            for (Department d : originalList) {
                if (d.getTenPhong().toLowerCase().contains(keyword.toLowerCase())
                        || d.getMaPb().toLowerCase().contains(keyword.toLowerCase())) {
                    departmentList.add(d);
                }
            }
        }
        departmentAdapter.notifyDataSetChanged();
    }

    private void showDepartmentDialog(Department department, boolean isEdit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_department, null);
        AlertDialog dialog = createDepartmentDialog(view);

        EditText edtMaPb = view.findViewById(R.id.edtMaPb);
        EditText edtTenPhong = view.findViewById(R.id.edtTenPhong);
        EditText edtMoTa = view.findViewById(R.id.edtMoTa);
        Button btnSave = view.findViewById(R.id.btnSaveDepartment);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);
// gọi hàm về
        bindDepartmentData(department, isEdit, edtMaPb, edtTenPhong, edtMoTa);
        setupCloseButton(dialog, btnClose);
        setupSaveButton(dialog, department, isEdit, edtMaPb, edtTenPhong, edtMoTa, btnSave);
    }

    private AlertDialog createDepartmentDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void bindDepartmentData(Department department, boolean isEdit,
                                    EditText edtMaPb, EditText edtTenPhong, EditText edtMoTa) {
        if (isEdit && department != null) {
            edtMaPb.setText(department.getMaPb());
            edtTenPhong.setText(department.getTenPhong());
            edtMoTa.setText(department.getMoTa());
            edtMaPb.setEnabled(false);
        }
    }

    private void setupCloseButton(AlertDialog dialog, Button btnClose) {
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupSaveButton(AlertDialog dialog, Department department, boolean isEdit,
                                 EditText edtMaPb, EditText edtTenPhong, EditText edtMoTa,
                                 Button btnSave) {
        btnSave.setOnClickListener(v -> {
            String maPb = edtMaPb.getText().toString().trim();
            String tenPhong = edtTenPhong.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();

            if (!validateDepartmentInput(maPb, tenPhong, edtMaPb, edtTenPhong)) {
                return;
            }

            if (isEdit && department != null) {
                updateDepartment(dialog, department, tenPhong, moTa);
            } else {
                insertDepartment(dialog, maPb, tenPhong, moTa);
            }
        });
    }

    private boolean validateDepartmentInput(String maPb, String tenPhong,
                                            EditText edtMaPb, EditText edtTenPhong) {
        if (TextUtils.isEmpty(maPb)) {
            edtMaPb.setError("Không được để trống mã phòng ban");
            return false;
        }

        if (TextUtils.isEmpty(tenPhong)) {
            edtTenPhong.setError("Không được để trống tên phòng");
            return false;
        }

        return true;
    }

    private void updateDepartment(AlertDialog dialog, Department department,
                                  String tenPhong, String moTa) {
        department.setTenPhong(tenPhong);
        department.setMoTa(moTa);

        boolean result = departmentDAO.updateDepartment(department);
        if (result) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            loadDepartments();
            dialog.dismiss();
        }
    }

    private void insertDepartment(AlertDialog dialog, String maPb, String tenPhong, String moTa) {
        Department newDepartment = new Department();
        newDepartment.setMaPb(maPb);
        newDepartment.setTenPhong(tenPhong);
        newDepartment.setMoTa(moTa);

        boolean result = departmentDAO.insertDepartment(newDepartment);
        if (result) {
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            loadDepartments();
            dialog.dismiss();
        } else {
            Toast.makeText(this, "Mã phòng ban đã tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteDepartment(Department department) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa phòng ban")
                .setMessage("Bạn có chắc muốn xóa không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean result = departmentDAO.deleteDepartment(department.getIdPhongBan());

                    if (result) {
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadDepartments();
                    } else {
                        Toast.makeText(this, "Không thể xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}