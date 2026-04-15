package com.example.hrm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.dto.SalaryDTO;
import com.example.hrm.listeners.OnItemActionListener;

import java.util.List;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    private List<SalaryDTO> salaryList;
    private OnItemActionListener<SalaryDTO> listener;

    public SalaryAdapter(List<SalaryDTO> salaryList, OnItemActionListener<SalaryDTO> listener) {
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
        SalaryDTO dto = salaryList.get(position);

        holder.tvNhanVien.setText(dto.getHoTen());
        holder.tvMaNhanVien.setText(dto.getMaNv());
        holder.tvThangNam.setText("Tháng: " + dto.getThangNam());
        holder.tvNgayCong.setText("Sô ngày công: " + dto.getSoNgayCong());

        holder.tvThuong.setText("+ " + dto.getTongThuongDisplay());
        holder.tvPhat.setText("- " + dto.getTongPhatDisplay());
        holder.tvTongLuong.setText(dto.getTongLuongDisplay());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(dto);
        });

        holder.imgEditSalary.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(dto);
        });

        holder.imgDeleteSalary.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(dto);
        });
    }

    @Override
    public int getItemCount() {
        return salaryList != null ? salaryList.size() : 0;
    }

    public void updateList(List<SalaryDTO> newList) {
        this.salaryList = newList;
        notifyDataSetChanged();
    }

    public static class SalaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNhanVien, tvMaNhanVien, tvThangNam, tvNgayCong, tvThuong, tvPhat, tvTongLuong;
        ImageView imgEditSalary, imgDeleteSalary;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNhanVien = itemView.findViewById(R.id.tvTenNhanVien);
            tvMaNhanVien = itemView.findViewById(R.id.tvMaNhanVien);
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