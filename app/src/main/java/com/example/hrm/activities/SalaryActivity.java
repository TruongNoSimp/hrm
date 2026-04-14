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
import com.example.hrm.adapters.SalaryAdapter;
import com.example.hrm.dao.SalaryDAO;
import com.example.hrm.listeners.OnItemActionListener;
import com.example.hrm.models.Salary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SalaryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSalary;
    private FloatingActionButton fabAddSalary;
    private EditText edtSearchSalary;

    private SalaryDAO salaryDAO;
    private List<Salary> salaryList;
    private List<Salary> originalList;
    private SalaryAdapter salaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        initViews();
        initData();
        loadSalaries();
        initSearch();
        setupToolbar();
    }

    private void initViews() {
        recyclerViewSalary = findViewById(R.id.recyclerViewSalary);
        fabAddSalary = findViewById(R.id.fabAddSalary);
        edtSearchSalary = findViewById(R.id.edtSearchSalary);
    }

    private void initData() {
        salaryDAO = new SalaryDAO(this);
        salaryList = new ArrayList<>();
        originalList = new ArrayList<>();

        recyclerViewSalary.setLayoutManager(new LinearLayoutManager(this));

        salaryAdapter = new SalaryAdapter(salaryList, new OnItemActionListener<Salary>() {
            @Override
            public void onEdit(Salary salary) {
                showSalaryDialog(salary, true);
            }

            @Override
            public void onDelete(Salary salary) {
                confirmDeleteSalary(salary);
            }

            @Override
            public void onItemClick(Salary salary) {
                // chưa dùng thì để trống
            }
        });

        recyclerViewSalary.setAdapter(salaryAdapter);

        fabAddSalary.setOnClickListener(v -> showSalaryDialog(null, false));
    }

    private void loadSalaries() {
        salaryList.clear();
        originalList.clear();

        List<Salary> data = salaryDAO.getAllSalaries();
        salaryList.addAll(data);
        originalList.addAll(data);

        salaryAdapter.notifyDataSetChanged();
    }

    private void initSearch() {
        edtSearchSalary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSalaries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarSalary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void filterSalaries(String keyword) {
        salaryList.clear();

        if (keyword.isEmpty()) {
            salaryList.addAll(originalList);
        } else {
            String lowerKeyword = keyword.toLowerCase(Locale.getDefault());

            for (Salary salary : originalList) {
                String hoTen = salary.getHoTen() != null ? salary.getHoTen().toLowerCase(Locale.getDefault()) : "";
                String maNv = salary.getMaNv() != null ? salary.getMaNv().toLowerCase(Locale.getDefault()) : "";
                String thangNam = salary.getThangNam() != null ? salary.getThangNam().toLowerCase(Locale.getDefault()) : "";

                if (hoTen.contains(lowerKeyword)
                        || maNv.contains(lowerKeyword)
                        || thangNam.contains(lowerKeyword)) {
                    salaryList.add(salary);
                }
            }
        }

        salaryAdapter.notifyDataSetChanged();
    }

    private void showSalaryDialog(Salary salary, boolean isEdit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_salary, null);
        AlertDialog dialog = createSalaryDialog(view);

        Spinner spNhanVien = view.findViewById(R.id.spNhanVien);
        EditText edtThangNam = view.findViewById(R.id.edtThangNam);
        EditText edtPhuCap = view.findViewById(R.id.edtPhuCap);
        EditText edtKhauTru = view.findViewById(R.id.edtKhauTru);
        Button btnSave = view.findViewById(R.id.btnSaveSalary);
        Button btnClose = view.findViewById(R.id.btnCloseSalaryDialog);

        setupEmployeeSpinner(spNhanVien);
        setupMonthPicker(edtThangNam);
        bindSalaryData(salary, isEdit, spNhanVien, edtThangNam, edtPhuCap, edtKhauTru);
        setupCloseButton(dialog, btnClose);
        setupSaveButton(dialog, salary, isEdit, spNhanVien, edtThangNam, edtPhuCap, edtKhauTru, btnSave);
    }

    private AlertDialog createSalaryDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void bindSalaryData(Salary salary, boolean isEdit,
                                Spinner spNhanVien,
                                EditText edtThangNam,
                                EditText edtPhuCap,
                                EditText edtKhauTru) {
        if (isEdit && salary != null) {
            edtThangNam.setText(salary.getThangNam());
            edtPhuCap.setText(String.valueOf(salary.getPhuCap()));
            edtKhauTru.setText(String.valueOf(salary.getKhauTru()));

            setSpinnerToCurrentEmployee(spNhanVien, salary.getIdNv());

            spNhanVien.setEnabled(false);
            edtThangNam.setEnabled(false);
        }
    }

    private void setSpinnerToCurrentEmployee(Spinner spNhanVien, int idNv) {
        for (int i = 0; i < spNhanVien.getCount(); i++) {
            String item = spNhanVien.getItemAtPosition(i).toString();
            if (item.startsWith(idNv + " - ")) {
                spNhanVien.setSelection(i);
                break;
            }
        }
    }

    private void setupCloseButton(AlertDialog dialog, Button btnClose) {
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupSaveButton(AlertDialog dialog,
                                 Salary salary,
                                 boolean isEdit,
                                 Spinner spNhanVien,
                                 EditText edtThangNam,
                                 EditText edtPhuCap,
                                 EditText edtKhauTru,
                                 Button btnSave) {
        btnSave.setOnClickListener(v -> {
            String selectedEmployee = spNhanVien.getSelectedItem() != null
                    ? spNhanVien.getSelectedItem().toString().trim()
                    : "";

            int idNv = salaryDAO.extractEmployeeId(selectedEmployee);
            String thangNam = edtThangNam.getText().toString().trim();
            double phuCap = parseDouble(edtPhuCap);
            double khauTru = parseDouble(edtKhauTru);

            if (!validateSalaryInput(idNv, thangNam, edtThangNam)) {
                return;
            }

            if (isEdit && salary != null) {
                updateSalary(dialog, salary, phuCap, khauTru);
            } else {
                insertSalary(dialog, idNv, thangNam, phuCap, khauTru);
            }
        });
    }

    private boolean validateSalaryInput(int idNv, String thangNam, EditText edtThangNam) {
        if (idNv == -1) {
            Toast.makeText(this, "Vui lòng chọn nhân viên hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(thangNam)) {
            edtThangNam.setError("Không được để trống tháng/năm");
            return false;
        }

        return true;
    }

    private void updateSalary(AlertDialog dialog, Salary salary, double phuCap, double khauTru) {
        salary.setPhuCap(phuCap);
        salary.setKhauTru(khauTru);

        boolean result = salaryDAO.updateSalary(salary);
        if (result) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            loadSalaries();
            dialog.dismiss();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertSalary(AlertDialog dialog, int idNv, String thangNam, double phuCap, double khauTru) {
        Salary newSalary = new Salary();
        newSalary.setIdNv(idNv);
        newSalary.setThangNam(thangNam);
        newSalary.setPhuCap(phuCap);
        newSalary.setKhauTru(khauTru);

        boolean result = salaryDAO.insertSalary(newSalary);
        if (result) {
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            loadSalaries();
            dialog.dismiss();
        } else {
            Toast.makeText(this, "Lương tháng này đã tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEmployeeSpinner(Spinner spinner) {
        List<String> employeeList = salaryDAO.getAllEmployeeDisplayList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employeeList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupMonthPicker(EditText edtThangNam) {
        edtThangNam.setOnClickListener(v -> showMonthPickerDialog(edtThangNam));
    }

    private void showMonthPickerDialog(EditText edtThangNam) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String thangNam = String.format(Locale.getDefault(), "%02d/%d", month + 1, year);
                    edtThangNam.setText(thangNam);
                },
                currentYear,
                currentMonth,
                1
        );

        datePickerDialog.show();
    }

    private double parseDouble(EditText edt) {
        try {
            return Double.parseDouble(edt.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void confirmDeleteSalary(Salary salary) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lương")
                .setMessage("Bạn có chắc muốn xóa không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean result = salaryDAO.deleteSalary(salary.getIdLuong());

                    if (result) {
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadSalaries();
                    } else {
                        Toast.makeText(this, "Không thể xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}