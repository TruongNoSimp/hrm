package com.example.hrm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.models.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private final List<Employee> employeeList;
    private final OnEmployeeActionListener listener;

    public interface OnEmployeeActionListener {
        void onEdit(Employee employee);
        void onDelete(Employee employee);
        void onItemClick(Employee employee);
    }

    public EmployeeAdapter(List<Employee> employeeList, OnEmployeeActionListener listener) {
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        holder.tvMaNv.setText("Mã NV: " + employee.getMaNv());
        holder.tvHoTen.setText("Họ tên: " + employee.getHoTen());
        holder.tvPhongBan.setText("Phòng ban: " + (employee.getTenPhongBan() == null ? "" : employee.getTenPhongBan()));
        holder.tvChucVu.setText("Chức vụ: " + employee.getChucVu());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(employee);
            }
        });

        holder.imgEditEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(employee);
            }
        });

        holder.imgDeleteEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(employee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList != null ? employeeList.size() : 0;
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaNv, tvHoTen, tvPhongBan, tvChucVu;
        ImageView imgEditEmployee, imgDeleteEmployee;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaNv = itemView.findViewById(R.id.tvMaNv);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvPhongBan = itemView.findViewById(R.id.tvPhongBan);
            tvChucVu = itemView.findViewById(R.id.tvChucVu);
            imgEditEmployee = itemView.findViewById(R.id.imgEditEmployee);
            imgDeleteEmployee = itemView.findViewById(R.id.imgDeleteEmployee);
        }
    }
}