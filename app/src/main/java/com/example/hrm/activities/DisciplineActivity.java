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
import java.util.List;

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

        disciplineAdapter = new DisciplineAdapter(this, disciplineList, new OnItemActionListener<Discipline>() {
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
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_discipline, null);
        AlertDialog dialog = createKyLuatDialog(view);

        Spinner spNhanVien = view.findViewById(R.id.spNhanVien);
        EditText edtNgayQuyetDinh = view.findViewById(R.id.edtNgayQuyetDinh);
        EditText edtHinhThuc = view.findViewById(R.id.edtHinhThuc);
        EditText edtSoTienPhat = view.findViewById(R.id.edtSoTienPhat);
        EditText edtLyDo = view.findViewById(R.id.edtLyDo);
        Button btnSave = view.findViewById(R.id.btnSaveKyLuat);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        edtNgayQuyetDinh.setFocusable(false);
        edtNgayQuyetDinh.setClickable(true);
        edtNgayQuyetDinh.setOnClickListener(v -> showDatePicker(edtNgayQuyetDinh));

        employeeList = getEmployeeList();
        setupEmployeeSpinner(spNhanVien, employeeList);
        bindKyLuatData(discipline, isEdit, spNhanVien, edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo);
        setupCloseButton(dialog, btnClose);
        setupSaveKyLuatButton(dialog, discipline, isEdit, spNhanVien,
                edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo, btnSave);
    }

    private AlertDialog createKyLuatDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private List<Employee> getEmployeeList() {
        List<Employee> list = employeeDAO.getAllEmployees();
        return list != null ? list : new ArrayList<>();
    }

    private void setupEmployeeSpinner(Spinner spNhanVien, List<Employee> employeeList) {
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
    }

    private void bindKyLuatData(Discipline discipline, boolean isEdit,
                                Spinner spNhanVien,
                                EditText edtNgayQuyetDinh,
                                EditText edtHinhThuc,
                                EditText edtSoTienPhat,
                                EditText edtLyDo) {
        if (isEdit && discipline != null) {
            edtNgayQuyetDinh.setText(discipline.getNgayQuyetDinh());
            edtHinhThuc.setText(discipline.getHinhThuc());
            edtSoTienPhat.setText(String.valueOf(discipline.getSoTienPhat()));
            edtLyDo.setText(discipline.getLyDo());

            int selectedPosition = findSelectedEmployeePosition(discipline.getIdNhanVien());
            spNhanVien.setSelection(selectedPosition);
        }
    }

    private int findSelectedEmployeePosition(int idNhanVien) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getIdNv() == idNhanVien) {
                return i;
            }
        }
        return 0;
    }

    private void setupCloseButton(AlertDialog dialog, Button btnClose) {
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupSaveKyLuatButton(AlertDialog dialog, Discipline discipline, boolean isEdit,
                                       Spinner spNhanVien,
                                       EditText edtNgayQuyetDinh,
                                       EditText edtHinhThuc,
                                       EditText edtSoTienPhat,
                                       EditText edtLyDo,
                                       Button btnSave) {
        btnSave.setOnClickListener(v -> {
            if (employeeList.isEmpty()) {
                Toast.makeText(this, "Chưa có nhân viên để chọn", Toast.LENGTH_SHORT).show();
                return;
            }

            String ngayQuyetDinh = edtNgayQuyetDinh.getText().toString().trim();
            String hinhThuc = edtHinhThuc.getText().toString().trim();
            String soTienPhatStr = edtSoTienPhat.getText().toString().trim();
            String lyDo = edtLyDo.getText().toString().trim();

            Double soTienPhat = validateKyLuatInput(
                    ngayQuyetDinh, hinhThuc, soTienPhatStr, lyDo,
                    edtNgayQuyetDinh, edtHinhThuc, edtSoTienPhat, edtLyDo
            );

            if (soTienPhat == null) {
                return;
            }

            Employee selectedEmployee = employeeList.get(spNhanVien.getSelectedItemPosition());

            if (isEdit && discipline != null) {
                updateKyLuat(dialog, discipline, selectedEmployee, ngayQuyetDinh, hinhThuc, soTienPhat, lyDo);
            } else {
                insertKyLuat(dialog, selectedEmployee, ngayQuyetDinh, hinhThuc, soTienPhat, lyDo);
            }
        });
    }

    private Double validateKyLuatInput(String ngayQuyetDinh,
                                       String hinhThuc,
                                       String soTienPhatStr,
                                       String lyDo,
                                       EditText edtNgayQuyetDinh,
                                       EditText edtHinhThuc,
                                       EditText edtSoTienPhat,
                                       EditText edtLyDo) {
        if (TextUtils.isEmpty(ngayQuyetDinh)) {
            edtNgayQuyetDinh.setError("Không được để trống ngày quyết định");
            return null;
        }

        if (TextUtils.isEmpty(hinhThuc)) {
            edtHinhThuc.setError("Không được để trống hình thức kỷ luật");
            return null;
        }

        if (TextUtils.isEmpty(soTienPhatStr)) {
            edtSoTienPhat.setError("Không được để trống số tiền phạt");
            return null;
        }

        if (TextUtils.isEmpty(lyDo)) {
            edtLyDo.setError("Không được để trống lý do");
            return null;
        }

        double soTienPhat;
        try {
            soTienPhat = Double.parseDouble(soTienPhatStr);
        } catch (Exception e) {
            edtSoTienPhat.setError("Số tiền phạt không hợp lệ");
            return null;
        }

        if (soTienPhat < 0) {
            edtSoTienPhat.setError("Số tiền phạt phải >= 0");
            return null;
        }

        return soTienPhat;
    }

    private void updateKyLuat(AlertDialog dialog,
                              Discipline discipline,
                              Employee selectedEmployee,
                              String ngayQuyetDinh,
                              String hinhThuc,
                              double soTienPhat,
                              String lyDo) {
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

    private void insertKyLuat(AlertDialog dialog,
                              Employee selectedEmployee,
                              String ngayQuyetDinh,
                              String hinhThuc,
                              double soTienPhat,
                              String lyDo) {
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

    private void showDatePicker(EditText editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // Nếu EditText đã có ngày, cố gắng parse để hiển thị đúng ngày đó trên lịch
        String currentText = editText.getText().toString();
        if (!currentText.isEmpty()) {
            try {
                String[] parts = currentText.split("-");
                if (parts.length == 3) {
                    calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format định dạng yyyy-MM-dd để lưu xuống database cho chuẩn
                    String date = String.format(java.util.Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}