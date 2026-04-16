package com.example.hrm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hrm.R;
import com.example.hrm.dao.DepartmentDAO;
import com.google.android.material.navigation.NavigationView;

import com.example.hrm.dao.EmployeeDAO;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView tvTotalDepartments;
    private TextView tvTotalEmployees;
    private TextView tvPresentToday;

    private TextView tvAccountName;
    private TextView tvAccountRole;
    private TextView btnViewProfile;
    private TextView btnLogout;

    private CardView cardDepartments;
    private CardView cardEmployees;
    private CardView cardPresent;

    private LinearLayout menuDepartment;
    private LinearLayout menuEmployee;
    private LinearLayout menuAttendance;
    private LinearLayout menuSalary;
    private LinearLayout menuReward;
    private LinearLayout menuDiscipline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupToolbarAndDrawer();
        setupAccountFooter();
        loadDashboardData();
        setupClickEvents();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        tvTotalDepartments = findViewById(R.id.tvTotalDepartments);
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees);
        tvPresentToday = findViewById(R.id.tvPresentToday);

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountRole = findViewById(R.id.tvAccountRole);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnLogout = findViewById(R.id.btnLogout);

        cardDepartments = findViewById(R.id.cardDepartments);
        cardEmployees = findViewById(R.id.cardEmployees);
        cardPresent = findViewById(R.id.cardPresent);

        menuDepartment = findViewById(R.id.menuDepartment);
        menuEmployee = findViewById(R.id.menuEmployee);
        menuAttendance = findViewById(R.id.menuAttendance);
        menuSalary = findViewById(R.id.menuSalary);
        menuReward = findViewById(R.id.menuReward);
        menuDiscipline = findViewById(R.id.menuDiscipline);
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

        //Lấy adminname và username. Nếu không thấy thì để mặc định là "Admin" và "admin"
        String adminName = prefs.getString("adminname", "Người dùng");
        String username = prefs.getString("username", "admin");

        // 3. Set lên UI: Tên thật dòng to, Username dòng nhỏ (thêm @ cho nó giống mạng xã hội)
        tvAccountName.setText(adminName);
        tvAccountRole.setText("@" + username);

        btnViewProfile.setOnClickListener(v ->
                Toast.makeText(this, "Thông tin của " + adminName, Toast.LENGTH_SHORT).show()
        );

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
        try {
            int totalDepartments = getDepartmentCountFromDB();
            int totalEmployees = getEmployeeCountFromDB();
            int presentToday = getAttendanceCountToday();

            tvTotalDepartments.setText(String.valueOf(totalDepartments));
            tvTotalEmployees.setText(String.valueOf(totalEmployees));
            tvPresentToday.setText(String.valueOf(presentToday));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getDepartmentCountFromDB() {
        try {
            DepartmentDAO departmentDAO = new DepartmentDAO(this);
            return departmentDAO.getAllDepartments().size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getEmployeeCountFromDB() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO(this);
            return employeeDAO.getEmployeeCountFromDB();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getAttendanceCountToday() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO(this);
            return employeeDAO.getAttendanceCountToday();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void setupClickEvents() {
        try {
            if (cardDepartments != null) cardDepartments.setOnClickListener(v -> openDepartment());
            if (cardEmployees != null) cardEmployees.setOnClickListener(v -> openEmployee());
            if (cardPresent != null) cardPresent.setOnClickListener(v -> openAttendance());

            if (menuDepartment != null) menuDepartment.setOnClickListener(v -> openDepartment());
            if (menuEmployee != null) menuEmployee.setOnClickListener(v -> openEmployee());
            if (menuAttendance != null) menuAttendance.setOnClickListener(v -> openAttendance());
            if (menuSalary != null) menuSalary.setOnClickListener(v -> openSalary());
            if (menuReward != null) menuReward.setOnClickListener(v -> openReward());
            if (menuDiscipline != null) menuDiscipline.setOnClickListener(v -> openDiscipline());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi setup click: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openEmployee() {
        startActivity(new Intent(HomeActivity.this, EmployeeActivity.class));
    }

    private void openDepartment() {
        startActivity(new Intent(HomeActivity.this, DepartmentActivity.class));
    }

    private void openAttendance() {
        startActivity(new Intent(HomeActivity.this, AttendanceActivity.class));
    }

    private void openSalary() {
        startActivity(new Intent(HomeActivity.this, SalaryActivity.class));
    }

    private void openReward() {
        startActivity(new Intent(HomeActivity.this, RewardActivity.class));
    }

    private void openDiscipline() {
        startActivity(new Intent(HomeActivity.this, DisciplineActivity.class));
    }

    private void openSettings() {
        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}