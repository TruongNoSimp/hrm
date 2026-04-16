package com.example.hrm.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.AttendanceAdapter;
import com.example.hrm.adapters.AttendanceHistoryAdapter;
import com.example.hrm.dao.AttendanceDAO;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.dto.EmployeeAttendanceDTO;
import com.example.hrm.dto.AttendanceHistoryDTO;
import com.example.hrm.models.Employee;
import com.example.hrm.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView rvAttendance;
    private FloatingActionButton fabHistory;
    private EditText edtSearch;
    private AttendanceDAO attendanceDAO;
    private EmployeeDAO employeeDAO;

    private List<EmployeeAttendanceDTO> employeeList;
    private List<EmployeeAttendanceDTO> originalList;
    private AttendanceAdapter adapter;

    private String historyViewingDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        initViews();
        initData();
        initActions();
        setupToolbar();
    }

    private void initViews() {
        rvAttendance = findViewById(R.id.recyclerViewAttendance);
        edtSearch = findViewById(R.id.edtSearchAttendance);
        fabHistory = findViewById(R.id.fabHistory);

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        attendanceDAO = new AttendanceDAO(this);
        employeeDAO = new EmployeeDAO(this);

        originalList = attendanceDAO.getAllEmployeesWithAttendance();
        employeeList = new ArrayList<>(originalList);

        adapter = new AttendanceAdapter(this, employeeList, this::handleCheckIn);
        //this::handleCheckIn tương đương override onCheckInClick
        rvAttendance.setAdapter(adapter);
    }

    private void initActions() {
        edtSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Lọc theo tên nhân viên (Autocomplete)
                search(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fabHistory.setOnClickListener(v -> showAttendanceHistory());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarAttendance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void handleCheckIn(Employee employee, String selectedTime) {
        long result = attendanceDAO.markAttendance(employee.getIdNv(), selectedTime);
        if (result != -1) {
            Toast.makeText(this, "Chấm công thành công: " + employee.getHoTen(), Toast.LENGTH_SHORT).show();
            loadLatestData();
        } else {
            Toast.makeText(this, "Lỗi Database!", Toast.LENGTH_SHORT).show();
        }
    }

    private void search(String query) {
        employeeList.clear();
        for (EmployeeAttendanceDTO dto : originalList) {
            Employee e = dto.getEmployee();

            String name = e.getHoTen().toLowerCase();
            String department = dto.getTenPhongBan().toLowerCase();
            String search = query.toLowerCase();

            if (name.contains(search) || department.contains(search)) {
                employeeList.add(dto);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadLatestData() {
        originalList = attendanceDAO.getAllEmployeesWithAttendance();
        employeeList.clear();
        employeeList.addAll(originalList);
        adapter.notifyDataSetChanged();
    }

    public void showAttendanceHistory() {
        historyViewingDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_attendance_history, null);

        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        TextView tvDate = view.findViewById(R.id.tvSelectedDateHistory);
        LinearLayout btnPickDate = view.findViewById(R.id.btnPickDateHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        tvDate.setText("Ngày: " + DateUtils.formatDisplayDate(this, historyViewingDate));

        updateHistoryList(rvHistory, historyViewingDate);

        // Sự kiện chọn ngày
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                historyViewingDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selected.getTime());

                tvDate.setText("Ngày: " + DateUtils.formatDisplayDate(this, historyViewingDate));
                updateHistoryList(rvHistory, historyViewingDate);

            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void updateHistoryList(RecyclerView rv, String date) {
        List<AttendanceHistoryDTO> list = attendanceDAO.getHistoryByDateWithDTO(date);
        AttendanceHistoryAdapter adapterHistory = new AttendanceHistoryAdapter(this, list);
        rv.setAdapter(adapterHistory);

        if (list.isEmpty()) {
            String displayDate = DateUtils.formatDisplayDate(this, date);
            Toast.makeText(this, "Không có dữ liệu ngày " + displayDate, Toast.LENGTH_SHORT).show();
        }
    }
}
