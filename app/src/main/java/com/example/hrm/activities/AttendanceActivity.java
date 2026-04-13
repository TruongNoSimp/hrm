package com.example.hrm.activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.adapters.AttendanceAdapter;
import com.example.hrm.adapters.AttendanceHistoryAdapter;
import com.example.hrm.dao.AttendanceDAO;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.dto.EmployeeAttendanceDTO;
import com.example.hrm.dto.AttendanceHistoryDTO;
import com.example.hrm.models.Attendance;
import com.example.hrm.models.Employee;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        initViews();
        initData();
        initActions();
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
                filterList(s.toString());
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

    private void handleCheckIn(Employee employee, String selectedTime) {
        long result = attendanceDAO.markAttendance(employee.getIdNv(), selectedTime);
        if (result != -1) {
            Toast.makeText(this, "Chấm công thành công: " + employee.getHoTen(), Toast.LENGTH_SHORT).show();
            loadLatestData();
        } else {
            Toast.makeText(this, "Lỗi Database!", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterList(String query) {
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

    public void showAttendanceHistory() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<AttendanceHistoryDTO> list = attendanceDAO.getHistoryByDateWithDTO(today);

        if (list.isEmpty()) {
            Toast.makeText(this, "Chưa có ai chấm công hôm nay!", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_history_bottom_sheet, null);

        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        rvHistory.setAdapter(new AttendanceHistoryAdapter(this, list));

        dialog.setContentView(view);
        dialog.show();
    }

    private void search(String query) {
        employeeList.clear();
        for (EmployeeAttendanceDTO e : originalList) {
            if (e.getEmployee().getHoTen().toLowerCase().contains(query.toLowerCase())) {
                employeeList.add(e);
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
}
