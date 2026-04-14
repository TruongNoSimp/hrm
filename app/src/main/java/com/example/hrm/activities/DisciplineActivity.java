package com.example.hrm.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.example.hrm.listeners.OnItemActionListener;

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

        disciplineAdapter = new DisciplineAdapter(disciplineList, new OnItemActionListener<Discipline>() {
            @Override
            public void onEdit(Discipline discipline) {
                showKyLuatDialog(discipline, true);
            }

            @Override
            public void onDelete(Discipline discipline) {
                confirmDeleteKyLuat(discipline);
            }

            @Override
            public void onItemClick(Discipline discipline) {
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void filterKyLuat(String keyword) {
        disciplineList.clear();
        if (keyword.isEmpty()) {
            disciplineList.addAll(originalList);
        } else {
            String search = keyword.toLowerCase();
            for (Discipline k : originalList) {
                String ten = k.getTenNhanVien() == null ? "" : k.getTenNhanVien().toLowerCase();
                String ma = k.getMaNhanVien() == null ? "" : k.getMaNhanVien().toLowerCase();
                if (ten.contains(search) || ma.contains(search)) {
                    disciplineList.add(k);
                }
            }
        }
        disciplineAdapter.notifyDataSetChanged();
    }

    private void showKyLuatDialog(Discipline discipline, boolean isEdit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_discipline, null);
        AlertDialog dialog = createKyLuatDialog(view);

        Spinner spNhanVien = view.findViewById(R.id.spNhanVien);
        EditText edtNgayQuyetDinh = view.findViewById(R.id.edtNgayQuyetDinh);
        EditText edtHinhThuc = view.findViewById(R.id.edtHinhThuc);
        EditText edtSoTienPhat = view.findViewById(R.id.edtSoTienPhat);
        EditText edtLyDo = view.findViewById(R.id.edtLyDo);
        Button btnSave = view.findViewById(R.id.btnSaveKyLuat);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        setupDatePicker(edtNgayQuyetDinh);

        employeeList = getEmployeeList();
        setupEmployeeSpinner(spNhanVien, employeeList);
        bindKyLuatData(discipline, isEdit, spNhanVien, edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo);
        setupCloseButton(dialog, btnClose);
        setupSaveKyLuatButton(dialog, discipline, isEdit, spNhanVien, edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo, btnSave);
    }

    private void setupDatePicker(EditText edtNgayQuyetDinh) {
        edtNgayQuyetDinh.setFocusable(false);
        edtNgayQuyetDinh.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day);
                edtNgayQuyetDinh.setText(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private AlertDialog createKyLuatDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private List<Employee> getEmployeeList() {
        return employeeDAO.getAllEmployees();
    }

    private void setupEmployeeSpinner(Spinner spNhanVien, List<Employee> list) {
        List<String> names = new ArrayList<>();
        for (Employee e : list) {
            names.add(e.getMaNv() + " - " + e.getHoTen());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNhanVien.setAdapter(adapter);
    }

    private void bindKyLuatData(Discipline discipline, boolean isEdit, Spinner spNhanVien,
                                EditText edtNgayQuyetDinh, EditText edtHinhThuc,
                                EditText edtSoTienPhat, EditText edtLyDo) {
        if (isEdit && discipline != null) {
            edtNgayQuyetDinh.setText(discipline.getNgayQuyetDinh());
            edtHinhThuc.setText(discipline.getHinhThuc());
            edtSoTienPhat.setText(String.valueOf(discipline.getSoTienPhat()));
            edtLyDo.setText(discipline.getLyDo());

            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getIdNv() == discipline.getIdNhanVien()) {
                    spNhanVien.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupCloseButton(AlertDialog dialog, Button btnClose) {
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupSaveKyLuatButton(AlertDialog dialog, Discipline discipline, boolean isEdit,
                                       Spinner spNhanVien, EditText edtNgayQuyetDinh,
                                       EditText edtHinhThuc, EditText edtSoTienPhat,
                                       EditText edtLyDo, Button btnSave) {
        btnSave.setOnClickListener(v -> {
            if (employeeList.isEmpty()) {
                Toast.makeText(this, "Vui lòng thêm nhân viên trước", Toast.LENGTH_SHORT).show();
                return;
            }

            String ngayQuyetDinh = edtNgayQuyetDinh.getText().toString().trim();
            String hinhThuc = edtHinhThuc.getText().toString().trim();
            String soTienStr = edtSoTienPhat.getText().toString().trim();
            String lyDo = edtLyDo.getText().toString().trim();

            Double soTienPhat = validateKyLuatInput(ngayQuyetDinh, hinhThuc, soTienStr, lyDo,
                    edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo);

            if (soTienPhat == null) return;

            Employee selectedEmployee = employeeList.get(spNhanVien.getSelectedItemPosition());

            if (isEdit && discipline != null) {
                updateExistingKyLuat(dialog, discipline, selectedEmployee, ngayQuyetDinh, hinhThuc, soTienPhat, lyDo);
            } else {
                addNewKyLuat(dialog, selectedEmployee, ngayQuyetDinh, hinhThuc, soTienPhat, lyDo);
            }
        });
    }

    private Double validateKyLuatInput(String ngayQuyetDinh, String hinhThuc, String soTienStr, String lyDo,
                                       EditText edtNgayQuyetDinh, EditText edtHinhThuc,
                                       EditText edtSoTienPhat, EditText edtLyDo) {
        if (TextUtils.isEmpty(ngayQuyetDinh)) {
            edtNgayQuyetDinh.setError("Vui lòng nhập ngày");
            return null;
        }
        if (TextUtils.isEmpty(hinhThuc)) {
            edtHinhThuc.setError("Vui lòng nhập hình thức");
            return null;
        }
        if (TextUtils.isEmpty(soTienStr)) {
            edtSoTienPhat.setError("Vui lòng nhập số tiền");
            return null;
        }
        if (TextUtils.isEmpty(lyDo)) {
            edtLyDo.setError("Vui lòng nhập lý do");
            return null;
        }

        try {
            double soTien = Double.parseDouble(soTienStr);
            if (soTien < 0) {
                edtSoTienPhat.setError("Số tiền không được âm");
                return null;
            }
            return soTien;
        } catch (NumberFormatException e) {
            edtSoTienPhat.setError("Số tiền không hợp lệ");
            return null;
        }
    }

    private void updateExistingKyLuat(AlertDialog dialog, Discipline discipline, Employee selectedEmployee,
                                      String ngayQuyetDinh, String hinhThuc, double soTienPhat, String lyDo) {
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
    }

    private void addNewKyLuat(AlertDialog dialog, Employee selectedEmployee, String ngayQuyetDinh, String hinhThuc,
                              double soTienPhat, String lyDo) {
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