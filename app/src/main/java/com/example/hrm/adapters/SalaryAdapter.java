package com.example.hrm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.models.Salary;
import com.example.hrm.listeners.OnItemActionListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    private List<Salary> salaryList;
    private OnItemActionListener<Salary> listener;

    public SalaryAdapter(List<Salary> salaryList, OnItemActionListener<Salary> listener) {
        this.salaryList = salaryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salary, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        Salary salary = salaryList.get(position);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        holder.tvNhanVien.setText("Nhân viên: " + salary.getMaNv() + " - " + salary.getHoTen());
        holder.tvThangNam.setText("Tháng: " + salary.getThangNam());
        holder.tvNgayCong.setText("Ngày công: " + salary.getSoNgayCong());
        holder.tvThuong.setText("Thưởng: " + formatter.format(salary.getTongThuong()));
        holder.tvPhat.setText("Phạt: " + formatter.format(salary.getTongPhat()));
        holder.tvTongLuong.setText("Tổng lương: " + formatter.format(salary.getTongLuong()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(salary);
            }
        });

        holder.imgEditSalary.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(salary);
            }
        });

        holder.imgDeleteSalary.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(salary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salaryList != null ? salaryList.size() : 0;
    }

    public static class SalaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNhanVien, tvThangNam, tvNgayCong, tvThuong, tvPhat, tvTongLuong;
        ImageView imgEditSalary, imgDeleteSalary;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNhanVien = itemView.findViewById(R.id.tvNhanVien);
            tvThangNam = itemView.findViewById(R.id.tvThangNam);
            tvNgayCong = itemView.findViewById(R.id.tvNgayCong);
            tvThuong = itemView.findViewById(R.id.tvThuong);
            tvPhat = itemView.findViewById(R.id.tvPhat);
            tvTongLuong = itemView.findViewById(R.id.tvTongLuong);

            imgEditSalary = itemView.findViewById(R.id.imgEditSalary);
            imgDeleteSalary = itemView.findViewById(R.id.imgDeleteSalary);
        }
    }
}