package com.example.hrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.models.Discipline;

import java.text.DecimalFormat;
import java.util.List;
import com.example.hrm.listeners.OnItemActionListener;
import com.example.hrm.models.Employee;
import com.example.hrm.utils.DateUtils;

public class DisciplineAdapter extends RecyclerView.Adapter<DisciplineAdapter.KyLuatViewHolder> {

    private List<Discipline> disciplineList;
    private OnItemActionListener<Discipline> listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private Context context;

    public DisciplineAdapter(Context context, List<Discipline> disciplineList, OnItemActionListener<Discipline> listener) {
        this.context = context;
        this.disciplineList = disciplineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KyLuatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discipline, parent, false);
        return new KyLuatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KyLuatViewHolder holder, int position) {
        Discipline discipline = disciplineList.get(position);

        holder.tvTenNhanVien.setText(discipline.getTenNhanVien());
        holder.tvMaNhanVien.setText("Mã NV: " + discipline.getMaNhanVien());

        String displayDate = DateUtils.formatDisplayDate(context, discipline.getNgayQuyetDinh());
        holder.tvNgayQuyetDinh.setText(displayDate);

        holder.tvHinhThuc.setText(discipline.getHinhThuc());
        holder.tvSoTienPhat.setText("- " + decimalFormat.format(discipline.getSoTienPhat()) + " VNĐ");
        holder.tvLyDo.setText("Lý do: " + discipline.getLyDo());

        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(discipline);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(discipline);
            }
        });
    }

    @Override
    public int getItemCount() {
        return disciplineList != null ? disciplineList.size() : 0;
    }

    public void setData(List<Discipline> newList) {
        this.disciplineList = newList;
        notifyDataSetChanged();
    }

    public static class KyLuatViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNhanVien, tvMaNhanVien, tvNgayQuyetDinh,
                tvHinhThuc, tvSoTienPhat, tvLyDo;
        ImageView ivEdit, ivDelete;

        public KyLuatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenNhanVien = itemView.findViewById(R.id.tvTenNhanVien);
            tvMaNhanVien = itemView.findViewById(R.id.tvMaNhanVien);
            tvNgayQuyetDinh = itemView.findViewById(R.id.tvNgayQuyetDinh);
            tvHinhThuc = itemView.findViewById(R.id.tvHinhThuc);
            tvSoTienPhat = itemView.findViewById(R.id.tvSoTienPhat);
            tvLyDo = itemView.findViewById(R.id.tvLyDo);

            ivEdit = itemView.findViewById(R.id.imgEdit);
            ivDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}