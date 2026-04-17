package com.example.hrm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.dto.TrainingDTO;
import com.example.hrm.listeners.OnTrainingActionListener;
import com.example.hrm.utils.DateUtils;

import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {
    private Context context;
    private List<TrainingDTO> list;
    private OnTrainingActionListener actionListener;

    public TrainingAdapter(Context context, List<TrainingDTO> list, OnTrainingActionListener actionListener) {
        this.context = context;
        this.list = list;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_training, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
        TrainingDTO dto = list.get(position);

        bindBasicInfo(holder, dto);
        bindStatusBadge(holder, dto.getStatus());
        setupClickListeners(holder, dto);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private void bindBasicInfo(TrainingViewHolder holder, TrainingDTO dto) {
        holder.tvCourse.setText(dto.getCourseName());
        holder.tvTeacher.setText("Giảng viên: " + dto.getTeacher());

        String startDate = DateUtils.formatDisplayDate(context, dto.getStartDate());
        String endDate = DateUtils.formatDisplayDate(context, dto.getEndDate());

        holder.tvTime.setText(startDate + " - " + endDate);

        holder.tvEmployeeName.setText("Học viên: " + dto.getEmployeeName());
    }

    private void bindStatusBadge(TrainingViewHolder holder, String status) {
        holder.tvStatus.setText(status); //
        int color;
        switch (status) {
            case "Xuất sắc":
                color = Color.parseColor("#4CAF50");
                break;
            case "Đạt":
                color = Color.parseColor("#2196F3");
                break;
            case "Đang học":
                color = Color.parseColor("#FF9800");
                break;
            default:
                color = Color.parseColor("#F44336");
                break;
        }
        holder.tvStatus.getBackground().setTint(color);
    }

    private void setupClickListeners(TrainingViewHolder holder, TrainingDTO dto) {
        if (actionListener == null) return;
        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(dto));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(dto));
    }

    public void filterList(List<TrainingDTO> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvStatus, tvTeacher, tvTime, tvEmployeeName, tvEmployeeCode;
        ImageButton btnEdit, btnDelete;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvCourseName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvTime = itemView.findViewById(R.id.tvTimeRange);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeCode = itemView.findViewById(R.id.tvEmployeeCode);
            btnEdit = itemView.findViewById(R.id.btnEditTraining);
            btnDelete = itemView.findViewById(R.id.btnDeleteTraining);
        }
    }
}
