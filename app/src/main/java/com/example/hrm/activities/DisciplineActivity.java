package com.example.hrm.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.DisciplineAdapter;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.dao.DisciplineDAO;
import com.example.hrm.models.Employee;
import com.example.hrm.models.Discipline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DisciplineActivity extends AppCompatActivity {

    private RecyclerView recyclerViewKyLuat;
    private FloatingActionButton fabAddKyLuat;
    private EditText edtSearchKyLuat;

    private DisciplineDAO disciplineDAO;
    private EmployeeDAO employeeDAO;

    private List<Discipline> disciplineList;
    private List<Discipline> originalList;
    private DisciplineAdapter disciplineAdapter;

    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline);

        initViews();
        initData();
        loadData();
        initSearch();
        setupToolbar();
    }

    private void initViews() {
        recyclerViewKyLuat = findViewById(R.id.recyclerViewKyLuat);
        fabAddKyLuat = findViewById(R.id.fabAddKyLuat);
        edtSearchKyLuat = findViewById(R.id.edtSearchKyLuat);
    }

    private void initData() {
        disciplineDAO = new DisciplineDAO(this);
        employeeDAO = new EmployeeDAO(this);

        disciplineList = new ArrayList<>();
        originalList = new ArrayList<>();
        employeeList = new ArrayList<>();

        recyclerViewKyLuat.setLayoutManager(new LinearLayoutManager(this));

        disciplineAdapter = new DisciplineAdapter(disciplineList, new DisciplineAdapter.OnKyLuatActionListener() {
            @Override
            public void onEdit(Discipline discipline) {
                showKyLuatDialog(discipline, true);
            }

            @Override
            public void onDelete(Discipline discipline) {
                confirmDeleteKyLuat(discipline);
            }
        });

        recyclerViewKyLuat.setAdapter(disciplineAdapter);

        fabAddKyLuat.setOnClickListener(v -> showKyLuatDialog(null, false));
    }

    private void loadData() {
        disciplineList.clear();
        originalList.clear();

        List<Discipline> data = disciplineDAO.getAllKyLuat();
        disciplineList.addAll(data);
        originalList.addAll(data);

        disciplineAdapter.notifyDataSetChanged();
    }

    private void initSearch() {
        edtSearchKyLuat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKyLuat(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarKyLuat);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void filterKyLuat(String keyword) {
        disciplineList.clear();

        if (keyword.isEmpty()) {
            disciplineList.addAll(originalList);
        } else {
            for (Discipline k : originalList) {
                String tenNhanVien = k.getTenNhanVien() == null ? "" : k.getTenNhanVien().toLowerCase();
                String maNhanVien = k.getMaNhanVien() == null ? "" : k.getMaNhanVien().toLowerCase();
                String search = keyword.toLowerCase();

                if (tenNhanVien.contains(search) || maNhanVien.contains(search)) {
                    disciplineList.add(k);
                }
            }
        }

        disciplineAdapter.notifyDataSetChanged();
    }

    private void showKyLuatDialog(Discipline discipline, boolean isEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_discipline, null);
        builder.setView(view);

        Spinner spNhanVien = view.findViewById(R.id.spNhanVien);
        EditText edtNgayQuyetDinh = view.findViewById(R.id.edtNgayQuyetDinh);
        EditText edtHinhThuc = view.findViewById(R.id.edtHinhThuc);
        EditText edtSoTienPhat = view.findViewById(R.id.edtSoTienPhat);
        EditText edtLyDo = view.findViewById(R.id.edtLyDo);
        Button btnSave = view.findViewById(R.id.btnSaveKyLuat);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        AlertDialog dialog = builder.create();
        dialog.show();

        employeeList = employeeDAO.getAllEmployees();
        if (employeeList == null) {
            employeeList = new ArrayList<>();
        }

        List<String> employeeNames = new ArrayList<>();
        for (Employee employee : employeeList) {
            employeeNames.add(employee.getMaNv() + " - " + employee.getHoTen());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employeeNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNhanVien.setAdapter(spinnerAdapter);

        if (isEdit && discipline != null) {
            edtNgayQuyetDinh.setText(discipline.getNgayQuyetDinh());
            edtHinhThuc.setText(discipline.getHinhThuc());
            edtSoTienPhat.setText(String.valueOf(discipline.getSoTienPhat()));
            edtLyDo.setText(discipline.getLyDo());

            int selectedPosition = 0;
            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getIdNv() == discipline.getIdNhanVien()) {
                    selectedPosition = i;
                    break;
                }
            }
            spNhanVien.setSelection(selectedPosition);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            if (employeeList.isEmpty()) {
                Toast.makeText(this, "Chưa có nhân viên để chọn", Toast.LENGTH_SHORT).show();
                return;
            }

            String ngayQuyetDinh = edtNgayQuyetDinh.getText().toString().trim();
            String hinhThuc = edtHinhThuc.getText().toString().trim();
            String soTienPhatStr = edtSoTienPhat.getText().toString().trim();
            String lyDo = edtLyDo.getText().toString().trim();

            if (TextUtils.isEmpty(ngayQuyetDinh)) {
                edtNgayQuyetDinh.setError("Không được để trống ngày quyết định");
                return;
            }

            if (TextUtils.isEmpty(hinhThuc)) {
                edtHinhThuc.setError("Không được để trống hình thức kỷ luật");
                return;
            }

            if (TextUtils.isEmpty(soTienPhatStr)) {
                edtSoTienPhat.setError("Không được để trống số tiền phạt");
                return;
            }

            if (TextUtils.isEmpty(lyDo)) {
                edtLyDo.setError("Không được để trống lý do");
                return;
            }

            double soTienPhat;
            try {
                soTienPhat = Double.parseDouble(soTienPhatStr);
            } catch (Exception e) {
                edtSoTienPhat.setError("Số tiền phạt không hợp lệ");
                return;
            }

            if (soTienPhat < 0) {
                edtSoTienPhat.setError("Số tiền phạt phải >= 0");
                return;
            }

            Employee selectedEmployee = employeeList.get(spNhanVien.getSelectedItemPosition());

            if (isEdit && discipline != null) {
                discipline.setIdNhanVien(selectedEmployee.getIdNv());
                discipline.setNgayQuyetDinh(ngayQuyetDinh);
                discipline.setHinhThuc(hinhThuc);
                discipline.setSoTienPhat(soTienPhat);
                discipline.setLyDo(lyDo);

                int result = disciplineDAO.updateKyLuat(discipline);
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Discipline newDiscipline = new Discipline();
                newDiscipline.setIdNhanVien(selectedEmployee.getIdNv());
                newDiscipline.setNgayQuyetDinh(ngayQuyetDinh);
                newDiscipline.setHinhThuc(hinhThuc);
                newDiscipline.setSoTienPhat(soTienPhat);
                newDiscipline.setLyDo(lyDo);

                long result = disciplineDAO.insertKyLuat(newDiscipline);
                if (result > 0) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void confirmDeleteKyLuat(Discipline discipline) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa kỷ luật")
                .setMessage("Bạn có chắc muốn xóa không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = disciplineDAO.deleteKyLuat(discipline.getIdKyLuat());

                    if (result > 0) {
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Không thể xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}