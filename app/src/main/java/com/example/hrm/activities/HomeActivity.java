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
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView tvTotalDepartments;
    private TextView tvTotalEmployees;
    private TextView tvPresentToday;
    private TextView tvLeaveToday;

    private TextView tvAccountName;
    private TextView tvAccountRole;
    private TextView btnViewProfile;
    private TextView btnLogout;

    private CardView cardDepartments;
    private CardView cardEmployees;
    private CardView cardPresent;
    private CardView cardLeave;

    private LinearLayout menuDepartment;
    private LinearLayout menuEmployee;
    private LinearLayout menuAttendance;
    private LinearLayout menuLeave;
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
        tvLeaveToday = findViewById(R.id.tvLeaveToday);

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountRole = findViewById(R.id.tvAccountRole);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnLogout = findViewById(R.id.btnLogout);

        cardDepartments = findViewById(R.id.cardDepartments);
        cardEmployees = findViewById(R.id.cardEmployees);
        cardPresent = findViewById(R.id.cardPresent);
        cardLeave = findViewById(R.id.cardLeave);

        menuDepartment = findViewById(R.id.menuDepartment);
        menuEmployee = findViewById(R.id.menuEmployee);
        menuAttendance = findViewById(R.id.menuAttendance);
        menuLeave = findViewById(R.id.menuLeave);
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
        int totalEmployees = 0;
        int presentToday = 0;
        int leaveToday = 0;

        tvTotalDepartments.setText(String.valueOf(totalDepartments));
        tvTotalEmployees.setText(String.valueOf(totalEmployees));
        tvPresentToday.setText(String.valueOf(presentToday));
        tvLeaveToday.setText(String.valueOf(leaveToday));
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

    private void setupClickEvents() {
        cardDepartments.setOnClickListener(v -> openDepartment());
        cardEmployees.setOnClickListener(v -> showFeatureMessage("Quản lý nhân viên"));
        cardPresent.setOnClickListener(v -> showFeatureMessage("Chấm công"));
        cardLeave.setOnClickListener(v -> showFeatureMessage("Nghỉ phép"));

        menuDepartment.setOnClickListener(v -> openDepartment());
        menuEmployee.setOnClickListener(v -> showFeatureMessage("Quản lý nhân viên"));
        menuAttendance.setOnClickListener(v -> showFeatureMessage("Chấm công"));
        menuLeave.setOnClickListener(v -> showFeatureMessage("Nghỉ phép"));
        menuReward.setOnClickListener(v -> showFeatureMessage("Khen thưởng"));
        menuDiscipline.setOnClickListener(v -> showFeatureMessage("Kỷ luật"));
    }

    private void openDepartment() {
        startActivity(new Intent(HomeActivity.this, DepartmentActivity.class));
    }

    private void showFeatureMessage(String featureName) {
        Toast.makeText(this, "Chức năng " + featureName + " sẽ làm sau", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Bạn đang ở Trang chủ", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_department) {
            openDepartment();

        } else if (id == R.id.nav_employee) {
            showFeatureMessage("Quản lý nhân viên");

        } else if (id == R.id.nav_attendance) {
            showFeatureMessage("Chấm công");

        } else if (id == R.id.nav_leave) {
            showFeatureMessage("Nghỉ phép");

        } else if (id == R.id.nav_reward) {
            showFeatureMessage("Khen thưởng");

        } else if (id == R.id.nav_discipline) {
            showFeatureMessage("Kỷ luật");
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