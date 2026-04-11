package com.example.hrm.activities;

import android.app.AlertDialog;
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
import com.example.hrm.adapters.RewardAdapter;
import com.example.hrm.dao.EmployeeDAO;
import com.example.hrm.dao.RewardDAO;
import com.example.hrm.models.Employee;
import com.example.hrm.models.Reward;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RewardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewKhenThuong;
    private FloatingActionButton fabAddKhenThuong;
    private EditText edtSearchKhenThuong;

    private RewardDAO rewardDAO;
    private EmployeeDAO employeeDAO;

    private List<Reward> rewardList;
    private List<Reward> originalList;
    private RewardAdapter rewardAdapter;

    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        initViews();
        initData();
        loadData();
        initSearch();
        setupToolbar();
    }

    private void initViews() {
        recyclerViewKhenThuong = findViewById(R.id.recyclerViewKhenThuong);
        fabAddKhenThuong = findViewById(R.id.fabAddKhenThuong);
        edtSearchKhenThuong = findViewById(R.id.edtSearchKhenThuong);
    }

    private void initData() {
        rewardDAO = new RewardDAO(this);
        employeeDAO = new EmployeeDAO(this);

        rewardList = new ArrayList<>();
        originalList = new ArrayList<>();
        employeeList = new ArrayList<>();

        recyclerViewKhenThuong.setLayoutManager(new LinearLayoutManager(this));

        rewardAdapter = new RewardAdapter(rewardList, new RewardAdapter.OnKhenThuongActionListener() {
            @Override
            public void onEdit(Reward reward) {
                showKhenThuongDialog(reward, true);
            }

            @Override
            public void onDelete(Reward reward) {
                confirmDeleteKhenThuong(reward);
            }
        });

        recyclerViewKhenThuong.setAdapter(rewardAdapter);

        fabAddKhenThuong.setOnClickListener(v -> showKhenThuongDialog(null, false));
    }

    private void loadData() {
        rewardList.clear();
        originalList.clear();

        List<Reward> data = rewardDAO.getAllKhenThuong();
        rewardList.addAll(data);
        originalList.addAll(data);

        rewardAdapter.notifyDataSetChanged();
    }

    private void initSearch() {
        edtSearchKhenThuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKhenThuong(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarKhenThuong);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void filterKhenThuong(String keyword) {
        rewardList.clear();

        if (keyword.isEmpty()) {
            rewardList.addAll(originalList);
        } else {
            String search = keyword.toLowerCase();

            for (Reward kt : originalList) {
                String tenNhanVien = kt.getTenNhanVien() == null ? "" : kt.getTenNhanVien().toLowerCase();
                String maNhanVien = kt.getMaNhanVien() == null ? "" : kt.getMaNhanVien().toLowerCase();

                if (tenNhanVien.contains(search) || maNhanVien.contains(search)) {
                    rewardList.add(kt);
                }
            }
        }

        rewardAdapter.notifyDataSetChanged();
    }

    private void showKhenThuongDialog(Reward reward, boolean isEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_reward, null);
        builder.setView(view);

        Spinner spNhanVien = view.findViewById(R.id.spNhanVien);
        EditText edtNgayQuyetDinh = view.findViewById(R.id.edtNgayQuyetDinh);
        EditText edtHinhThuc = view.findViewById(R.id.edtHinhThuc);
        EditText edtSoTienThuong = view.findViewById(R.id.edtSoTienThuong);
        EditText edtLyDo = view.findViewById(R.id.edtLyDo);
        Button btnSave = view.findViewById(R.id.btnSaveKhenThuong);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        AlertDialog dialog = builder.create();
        dialog.show();

        employeeList = employeeDAO.getAllEmployees();
        if (employeeList == null) {
            employeeList = new ArrayList<>();
        }

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

        if (isEdit && reward != null) {
            edtNgayQuyetDinh.setText(reward.getNgayQuyetDinh());
            edtHinhThuc.setText(reward.getHinhThuc());
            edtSoTienThuong.setText(String.valueOf(reward.getSoTienThuong()));
            edtLyDo.setText(reward.getLyDo());

            int selectedPosition = 0;
            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getIdNv() == reward.getIdNhanVien()) {
                    selectedPosition = i;
                    break;
                }
            }
            spNhanVien.setSelection(selectedPosition);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            if (employeeList.isEmpty()) {
                Toast.makeText(this, "Chưa có nhân viên để chọn", Toast.LENGTH_SHORT).show();
                return;
            }

            String ngayQuyetDinh = edtNgayQuyetDinh.getText().toString().trim();
            String hinhThuc = edtHinhThuc.getText().toString().trim();
            String soTienThuongStr = edtSoTienThuong.getText().toString().trim();
            String lyDo = edtLyDo.getText().toString().trim();

            if (TextUtils.isEmpty(ngayQuyetDinh)) {
                edtNgayQuyetDinh.setError("Không được để trống ngày quyết định");
                return;
            }

            if (TextUtils.isEmpty(hinhThuc)) {
                edtHinhThuc.setError("Không được để trống hình thức khen thưởng");
                return;
            }

            if (TextUtils.isEmpty(soTienThuongStr)) {
                edtSoTienThuong.setError("Không được để trống số tiền thưởng");
                return;
            }

            if (TextUtils.isEmpty(lyDo)) {
                edtLyDo.setError("Không được để trống lý do");
                return;
            }

            double soTienThuong;
            try {
                soTienThuong = Double.parseDouble(soTienThuongStr);
            } catch (Exception e) {
                edtSoTienThuong.setError("Số tiền thưởng không hợp lệ");
                return;
            }

            if (soTienThuong < 0) {
                edtSoTienThuong.setError("Số tiền thưởng phải >= 0");
                return;
            }

            Employee selectedEmployee = employeeList.get(spNhanVien.getSelectedItemPosition());

            if (isEdit && reward != null) {
                reward.setIdNhanVien(selectedEmployee.getIdNv());
                reward.setNgayQuyetDinh(ngayQuyetDinh);
                reward.setHinhThuc(hinhThuc);
                reward.setSoTienThuong(soTienThuong);
                reward.setLyDo(lyDo);

                int result = rewardDAO.updateKhenThuong(reward);
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Reward newReward = new Reward();
                newReward.setIdNhanVien(selectedEmployee.getIdNv());
                newReward.setNgayQuyetDinh(ngayQuyetDinh);
                newReward.setHinhThuc(hinhThuc);
                newReward.setSoTienThuong(soTienThuong);
                newReward.setLyDo(lyDo);

                long result = rewardDAO.insertKhenThuong(newReward);
                if (result > 0) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void confirmDeleteKhenThuong(Reward reward) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa khen thưởng")
                .setMessage("Bạn có chắc muốn xóa không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = rewardDAO.deleteKhenThuong(reward.getIdKhenThuong());

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
