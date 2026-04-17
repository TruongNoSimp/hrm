package com.example.hrm.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.TrainingAdapter;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.dao.TrainingDAO;
import com.example.hrm.dto.TrainingDTO;
import com.example.hrm.listeners.OnTrainingActionListener;
import com.example.hrm.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity implements OnTrainingActionListener {
    private EditText edtCourseName, edtTeacherName, edtStartDate, edtEndDate;
    private Spinner spStaff, spResult;
    private Button btnSave, btnCancel;
    private Toolbar toolbarTraining;
    private EditText edtSearchTraining;
    private RecyclerView rvTraining;
    private FloatingActionButton fabAddTraining;
    private TrainingDAO trainingDAO;
    private TrainingAdapter adapter;
    private List<TrainingDTO> trainingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        initViews();
        initData();
        initActions();
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbarTraining);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarTraining.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        toolbarTraining = findViewById(R.id.toolbarTraining);
        edtSearchTraining = findViewById(R.id.edtSearchTraining);
        rvTraining = findViewById(R.id.rvTraining);
        fabAddTraining = findViewById(R.id.fabAddTraining);

        trainingDAO = new TrainingDAO(this);
        rvTraining.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        trainingList = trainingDAO.getAllTrainingInfo();
        adapter = new TrainingAdapter(this, trainingList, this);
        rvTraining.setAdapter(adapter);
    }

    private void initActions() {
        fabAddTraining.setOnClickListener(v -> showAddDialog());

        edtSearchTraining.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
        });
    }

    private void filterList(String query) {
        List<TrainingDTO> filtered = new ArrayList<>();
        for (TrainingDTO dto : trainingList) {
            if (dto.getCourseName().toLowerCase().contains(query.toLowerCase()) ||
                    dto.getEmployeeName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(dto);
            }
        }
        adapter.filterList(filtered);
    }

    public void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_training, null); //
        builder.setView(dialogView);

        edtCourseName = dialogView.findViewById(R.id.edtCourseName);
        edtTeacherName = dialogView.findViewById(R.id.edtTeacherName);
        edtStartDate = dialogView.findViewById(R.id.edtStartDate);
        edtEndDate = dialogView.findViewById(R.id.edtEndDate);
        spStaff = dialogView.findViewById(R.id.spStaff);
        spResult = dialogView.findViewById(R.id.spResult);
        btnSave = dialogView.findViewById(R.id.btnSave);
        btnCancel = dialogView.findViewById(R.id.btnCancel);

        setupSpinners(spStaff, spResult);

        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnSave.setOnClickListener(v -> {
            if (saveTraining()) {
                initData();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private boolean saveTraining() {
        if (!isInputValid()) {
            return false;
        }

        String name = edtCourseName.getText().toString().trim();
        String teacher = edtTeacherName.getText().toString().trim();
        String start = edtStartDate.getText().toString().trim();
        String end = edtEndDate.getText().toString().trim();
        Employee emp = (Employee) spStaff.getSelectedItem();
        String res = spResult.getSelectedItem().toString();

        boolean success = trainingDAO.insertTraining(name, teacher, start, end, String.valueOf(emp.getIdNv()), res);
        if (success) {
            Toast.makeText(this, "Đã thêm khóa học cho " + emp.getHoTen(), Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    private boolean isInputValid() {
        boolean isValid = true;

        if (edtCourseName.getText().toString().trim().isEmpty()) {
            edtCourseName.setError("Vui lòng nhập tên khóa học");
            edtCourseName.requestFocus();
            isValid = false;
        }

        if (edtTeacherName.getText().toString().trim().isEmpty()) {
            edtTeacherName.setError("Vui lòng nhập tên giảng viên");
            edtTeacherName.requestFocus();
            isValid = false;
        }

        if (edtStartDate.getText().toString().trim().isEmpty()) {
            edtStartDate.setError("Vui lòng chọn ngày bắt đầu");
            isValid = false;
        }

        if (edtEndDate.getText().toString().trim().isEmpty()) {
            edtEndDate.setError("Vui lòng chọn ngày kết thúc");
            isValid = false;
        }

        return isValid;
    }

    private void setupSpinners(Spinner spNV, Spinner spKQ) {
        List<Employee> listNV = new EmployeeDAO(this).getAllEmployees();
        ArrayAdapter<Employee> nvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listNV);
        spNV.setAdapter(nvAdapter);

        String[] results = {"Đang học", "Đạt", "Xuất sắc", "Không đạt"};
        spKQ.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, results));
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
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

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    @Override
    public void onEdit(TrainingDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_training, null);
        builder.setView(dialogView);

        // Mapping views (Dùng đúng ID mày đã sửa)
        EditText edtTen = dialogView.findViewById(R.id.edtCourseName);
        EditText edtGv = dialogView.findViewById(R.id.edtTeacherName);
        EditText edtBD = dialogView.findViewById(R.id.edtStartDate);
        EditText edtKT = dialogView.findViewById(R.id.edtEndDate);
        Spinner spNV = dialogView.findViewById(R.id.spStaff);
        Spinner spKQ = dialogView.findViewById(R.id.spResult);
        Button btnSaveEdit = dialogView.findViewById(R.id.btnSave);

        // Đổ dữ liệu cũ vào để người dùng sửa
        edtTen.setText(dto.getCourseName());
        edtGv.setText(dto.getTeacher());
        edtBD.setText(dto.getStartDate());
        edtKT.setText(dto.getEndDate());

        setupSpinners(spNV, spKQ);
        spNV.setEnabled(false);
        setSpinnerSelection(spNV, dto.getEmployeeCode());
        setSpinnerResultSelection(spKQ, dto.getStatus());

        // Add date picker listeners
        edtBD.setOnClickListener(v -> showDatePicker(edtBD));
        edtKT.setOnClickListener(v -> showDatePicker(edtKT));

        AlertDialog dialog = builder.create();

        btnSaveEdit.setOnClickListener(v -> {
            if (edtTen.getText().toString().trim().isEmpty()) {
                edtTen.setError("Không được để trống!");
                return;
            }

            boolean success = trainingDAO.updateTraining(
                    dto.getCourseId(),
                    edtTen.getText().toString().trim(),
                    edtGv.getText().toString().trim(),
                    edtBD.getText().toString().trim(),
                    edtKT.getText().toString().trim(),
                    dto.getEmployeeCode(),
                    spKQ.getSelectedItem().toString()
            );

            if (success) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                initData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Lỗi: Không thể cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setSpinnerSelection(Spinner spinner, String code) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Employee e = (Employee) spinner.getItemAtPosition(i);
            if (String.valueOf(e.getIdNv()).equals(code)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setSpinnerResultSelection(Spinner spinner, String status) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(status)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onDelete(TrainingDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn hủy phân công học viên [" + dto.getEmployeeName() +
                "] khỏi khóa [" + dto.getCourseName() + "] không?");

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            boolean isDeleted = trainingDAO.deleteTraining(dto.getCourseId(), dto.getEmployeeCode());

            if (isDeleted) {
                Toast.makeText(this, "Đã xóa bản ghi thành công", Toast.LENGTH_SHORT).show();
                initData();
            } else {
                Toast.makeText(this, "Lỗi: Không thể xóa dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
