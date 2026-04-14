package com.example.hrm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.hrm.dao.EmployeeDAO;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView tvTotalDepartments;
    private TextView tvTotalEmployees;
    private TextView tvPresentToday;
    private TextView tvSalary;

    private TextView tvAccountName;
    private TextView tvAccountRole;
    private TextView btnViewProfile;
    private TextView btnLogout;

    private CardView cardDepartments;
    private CardView cardEmployees;
    private CardView cardPresent;
    private CardView cardSalary;

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
        tvSalary = findViewById(R.id.tvSalary);

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountRole = findViewById(R.id.tvAccountRole);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnLogout = findViewById(R.id.btnLogout);

        cardDepartments = findViewById(R.id.cardDepartments);
        cardEmployees = findViewById(R.id.cardEmployees);
        cardPresent = findViewById(R.id.cardPresent);
        cardSalary = findViewById(R.id.cardSalary);

        menuDepartment = findViewById(R.id.menuDepartment);
        menuEmployee = findViewById(R.id.menuEmployee);
        menuAttendance = findViewById(R.id.menuAttendance);
        menuSalary = findViewById(R.id.menuSalary);
        menuReward = findViewById(R.id.menuReward);
        menuDiscipline = findViewById(R.id.menuDiscipline);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void setupAccountFooter() {
        tvAccountName.setText("truong");
        tvAccountRole.setText("Quản trị viên");

        btnViewProfile.setOnClickListener(v ->
                Toast.makeText(this, "Mở thông tin tài khoản", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v ->
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadDashboardData() {
        int totalDepartments = getDepartmentCountFromDB();
        int totalEmployees = getEmployeeCountFromDB();
        int presentToday = 0;

        tvTotalDepartments.setText(String.valueOf(totalDepartments));
        tvTotalEmployees.setText(String.valueOf(totalEmployees));
        tvPresentToday.setText(String.valueOf(presentToday));
        tvSalary.setText("=)))");
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

    private void setupClickEvents() {
        cardDepartments.setOnClickListener(v -> openDepartment());
        cardEmployees.setOnClickListener(v -> openEmployee());
        cardPresent.setOnClickListener(v -> openAttendance());
        cardSalary.setOnClickListener(v -> openSalary());

        menuDepartment.setOnClickListener(v -> openDepartment());
        menuEmployee.setOnClickListener(v -> openEmployee());
        menuAttendance.setOnClickListener(v -> openAttendance());
        menuSalary.setOnClickListener(v -> openSalary());
        menuReward.setOnClickListener(v -> openReward());
        menuDiscipline.setOnClickListener(v -> openDiscipline());
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