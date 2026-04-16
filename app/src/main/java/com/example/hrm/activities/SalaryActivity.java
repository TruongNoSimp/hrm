package com.example.hrm.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
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
import com.example.hrm.dto.SalaryDTO;
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
    private List<SalaryDTO> salaryList;
    private List<SalaryDTO> originalList;
    private SalaryAdapter salaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        initViews();
        salaryDAO = new SalaryDAO(this);

        setupRecyclerView();
        loadSalaries();
        setupSearch();

        fabAddSalary.setOnClickListener(v -> showSalaryDialog(null));
    }

    private void initViews() {
        recyclerViewSalary = findViewById(R.id.recyclerViewSalary);
        fabAddSalary = findViewById(R.id.fabAddSalary);
        edtSearchSalary = findViewById(R.id.edtSearchSalary);
        findViewById(R.id.toolbarSalary).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        salaryList = new ArrayList<>();
        salaryAdapter = new SalaryAdapter(salaryList, new OnItemActionListener<SalaryDTO>() {
            @Override
            public void onItemClick(SalaryDTO item) {}

            @Override
            public void onEdit(SalaryDTO item) {
                showSalaryDialog(item);
            }

            @Override
            public void onDelete(SalaryDTO item) {
                confirmDeleteSalary(item);
            }
        });
        recyclerViewSalary.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSalary.setAdapter(salaryAdapter);
    }

    private void loadSalaries() {
        salaryList = salaryDAO.getAllSalariesDTO();
        originalList = new ArrayList<>(salaryList);
        salaryAdapter.updateList(salaryList);
    }

    private void setupSearch() {
        edtSearchSalary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<SalaryDTO> filteredList = new ArrayList<>();
        for (SalaryDTO item : originalList) {
            if (item.getNhanVienDisplay().toLowerCase().contains(text.toLowerCase()) ||
                    item.getThangNam().contains(text)) {
                filteredList.add(item);
            }
        }
        salaryAdapter.updateList(filteredList);
    }

    private void showSalaryDialog(SalaryDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_salary, null);
        builder.setView(view);

        DialogViewHolder holder = new DialogViewHolder(view);
        AlertDialog dialog = builder.create();

        setupEmployeeSpinner(holder.spNhanVien);
        holder.edtThangNam.setOnClickListener(v -> showMonthPicker(holder.edtThangNam));

        if (dto != null) {
            bindDtoToDialog(holder, dto);
        }

        holder.btnClose.setOnClickListener(v -> dialog.dismiss());
        holder.btnSave.setOnClickListener(v -> handleSaveSalary(holder, dto, dialog));

        dialog.show();
    }

    private void bindDtoToDialog(DialogViewHolder holder, SalaryDTO dto) {
        holder.edtThangNam.setText(dto.getThangNam());
        holder.edtPhuCap.setText(String.valueOf(dto.getPhuCapRaw()));
        holder.edtKhauTru.setText(String.valueOf(dto.getKhauTruRaw()));

        ArrayAdapter adapter = (ArrayAdapter) holder.spNhanVien.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().contains(dto.getMaNv())) {
                holder.spNhanVien.setSelection(i);
                break;
            }
        }
        holder.btnSave.setText("Cập nhật");
    }

    private void handleSaveSalary(DialogViewHolder holder, SalaryDTO originalDto, AlertDialog dialog) {
        int idNv = salaryDAO.extractEmployeeId(holder.spNhanVien.getSelectedItem().toString());
        String thangNam = holder.edtThangNam.getText().toString();

        if (idNv == -1 || thangNam.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Salary s = new Salary();
        if (originalDto != null) s.setIdLuong(originalDto.getIdLuong());
        s.setIdNv(idNv);
        s.setThangNam(thangNam);
        s.setPhuCap(parseDouble(holder.edtPhuCap));
        s.setKhauTru(parseDouble(holder.edtKhauTru));

        if (salaryDAO.saveSalary(s, originalDto != null)) {
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            loadSalaries();
            dialog.dismiss();
        } else {
            Toast.makeText(this, "Lương tháng này đã tồn tại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEmployeeSpinner(Spinner spinner) {
        List<String> nvList = salaryDAO.getAllEmployeeDisplayList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nvList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showMonthPicker(EditText edt) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            edt.setText(String.format(Locale.getDefault(), "%02d/%d", month + 1, year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1).show();
    }

    private double parseDouble(EditText edt) {
        try { return Double.parseDouble(edt.getText().toString().trim()); } catch (Exception e) { return 0; }
    }

    private void confirmDeleteSalary(SalaryDTO dto) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bản ghi")
                .setMessage("Xóa bảng lương của " + dto.getHoTen() + "?")
                .setPositiveButton("Xóa", (d, w) -> {
                    if (salaryDAO.deleteSalary(dto.getIdLuong())) {
                        loadSalaries();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null).show();
    }

    private static class DialogViewHolder {
        Spinner spNhanVien;
        EditText edtThangNam, edtPhuCap, edtKhauTru;
        Button btnSave, btnClose;

        DialogViewHolder(View view) {
            spNhanVien = view.findViewById(R.id.spNhanVien);
            edtThangNam = view.findViewById(R.id.edtThangNam);
            edtPhuCap = view.findViewById(R.id.edtPhuCap);
            edtKhauTru = view.findViewById(R.id.edtKhauTru);
            btnSave = view.findViewById(R.id.btnSaveSalary);
            btnClose = view.findViewById(R.id.btnCloseSalaryDialog);
        }
    }
}