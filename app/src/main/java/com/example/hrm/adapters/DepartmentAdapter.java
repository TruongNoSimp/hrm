package com.example.hrm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.models.Department;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private List<Department> departmentList;
    private OnDepartmentActionListener listener;

    public interface OnDepartmentActionListener {
        void onEdit(Department department);
        void onDelete(Department department);
        void onItemClick(Department department);
    }

    public DepartmentAdapter(List<Department> departmentList, OnDepartmentActionListener listener) {
        this.departmentList = departmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        Department department = departmentList.get(position);

        holder.tvMaPb.setText("Mã PB: " + department.getMaPb());
        holder.tvTenPhong.setText("Tên phòng: " + department.getTenPhong());
        holder.tvMoTa.setText("Mô tả: " + department.getMoTa());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(department);
            }
        });

        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(department);
            }
        });

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(department);
            }
        });
    }

    @Override
    public int getItemCount() {
        return departmentList != null ? departmentList.size() : 0;
    }

    public static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaPb, tvTenPhong, tvMoTa;
        ImageView imgEdit, imgDelete;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMaPb = itemView.findViewById(R.id.tvMaPb);
            tvTenPhong = itemView.findViewById(R.id.tvTenPhong);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}