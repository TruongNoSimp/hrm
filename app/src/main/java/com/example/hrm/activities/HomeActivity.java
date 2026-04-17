package com.example.hrm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hrm.R;
import com.example.hrm.dao.DepartmentDAO;
import com.example.hrm.dao.TrainingDAO;
import com.google.android.material.navigation.NavigationView;

import com.example.hrm.dao.EmployeeDAO;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvTotalDepartments, tvTotalEmployees;
    private TextView tvHomeWorking, tvHomeNotCheckin, tvHomeTraining;
    private TextView tvAccountName, tvAccountRole, btnLogout;
    private CardView cardDepartments, cardEmployees;
    private LinearLayout menuDepartment, menuEmployee, menuAttendance, menuSalary, menuReward, menuDiscipline, menuTraining, menuSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupToolbarAndDrawer();
        setupAccountFooter();
        loadDashboardData();
        setupClickEvents();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        tvTotalDepartments = findViewById(R.id.tvTotalDepartments);
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees);

        tvHomeWorking = findViewById(R.id.tvHomeWorking);
        tvHomeNotCheckin = findViewById(R.id.tvHomeNotCheckin);
        tvHomeTraining = findViewById(R.id.tvHomeTraining);

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountRole = findViewById(R.id.tvAccountRole);
        btnLogout = findViewById(R.id.btnLogout);

        cardDepartments = findViewById(R.id.cardDepartments);
        cardEmployees = findViewById(R.id.cardEmployees);

        menuDepartment = findViewById(R.id.menuDepartment);
        menuEmployee = findViewById(R.id.menuEmployee);
        menuAttendance = findViewById(R.id.menuAttendance);
        menuSalary = findViewById(R.id.menuSalary);
        menuReward = findViewById(R.id.menuReward);
        menuDiscipline = findViewById(R.id.menuDiscipline);
        menuTraining = findViewById(R.id.menuTraining);
        menuSetting = findViewById(R.id.menuSetting);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void setupAccountFooter() {
        SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);

        String adminName = prefs.getString("adminname", "Người dùng");
        String username = prefs.getString("username", "admin");

        tvAccountName.setText(adminName);
        tvAccountRole.setText("@" + username);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLogin", false);
            editor.putBoolean("remember", false);
            editor.putString("username", "");
            editor.putString("adminname", "");
            editor.apply();

            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadDashboardData() {
        EmployeeDAO employeeDAO = new EmployeeDAO(this);
        DepartmentDAO departmentDAO = new DepartmentDAO(this);
        TrainingDAO trainingDAO = new TrainingDAO(this);

        try {
            int totalDepts = departmentDAO.getAllDepartments().size();
            int totalEmps = employeeDAO.getEmployeeCountFromDB();

            int workingToday = employeeDAO.getAttendanceCountToday();
            int notCheckin = totalEmps - workingToday;
            int trainingCount = trainingDAO.getAllTrainingInfo().size();

            tvTotalDepartments.setText(String.valueOf(totalDepts));
            tvTotalEmployees.setText(String.valueOf(totalEmps));

            tvHomeWorking.setText(String.valueOf(workingToday));
            tvHomeNotCheckin.setText(String.valueOf(notCheckin));
            tvHomeTraining.setText(String.valueOf(trainingCount));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi cập nhật Dashboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickEvents() {
        cardDepartments.setOnClickListener(v -> openDepartment());
        cardEmployees.setOnClickListener(v -> openEmployee());

        menuDepartment.setOnClickListener(v -> openDepartment());
        menuEmployee.setOnClickListener(v -> openEmployee());
        menuAttendance.setOnClickListener(v -> openAttendance());
        menuSalary.setOnClickListener(v -> openSalary());
        menuReward.setOnClickListener(v -> openReward());
        menuDiscipline.setOnClickListener(v -> openDiscipline());
        menuTraining.setOnClickListener(v -> openTraining());
        menuSetting.setOnClickListener(v -> openSettings());
    }

    private void openEmployee() {
        startActivity(new Intent(this, EmployeeActivity.class));
    }

    private void openDepartment() {
        startActivity(new Intent(this, DepartmentActivity.class));
    }

    private void openAttendance() {
        startActivity(new Intent(this, AttendanceActivity.class));
    }

    private void openSalary() {
        startActivity(new Intent(this, SalaryActivity.class));
    }

    private void openReward() {
        startActivity(new Intent(this, RewardActivity.class));
    }

    private void openDiscipline() {
        startActivity(new Intent(this, DisciplineActivity.class));
    }

    private void openTraining() {
        startActivity(new Intent(this, TrainingActivity.class));
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Bạn đang ở Trang chủ", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_department) {
            openDepartment();

        } else if (id == R.id.nav_employee) {
            openEmployee();

        } else if (id == R.id.nav_attendance) {
            openAttendance();

        } else if (id == R.id.nav_salary) {
            openSalary();

        } else if (id == R.id.nav_reward) {
            openReward();

        } else if (id == R.id.nav_discipline) {
            openDiscipline();
        } else if (id == R.id.nav_settings) {
            openSettings();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}