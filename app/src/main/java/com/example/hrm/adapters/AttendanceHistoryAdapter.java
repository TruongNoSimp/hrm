package com.example.hrm.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.dto.AttendanceHistoryDTO;
import com.example.hrm.utils.DateUtils;


import java.util.List;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<AttendanceHistoryDTO> attendanceList;

    public AttendanceHistoryAdapter(Context context, List<AttendanceHistoryDTO> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AttendanceHistoryDTO dto = attendanceList.get(position);

        holder.tvName.setText(dto.getEmployeeName());
        holder.tvTime.setText("Giờ vào: " + DateUtils.formatDisplayTime(context, dto.getGioVao()));

        // Set avatar
        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            try {
                holder.imgAvatar.setImageURI(Uri.parse(dto.getAvatar()));
            } catch (Exception e) {
                holder.imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_avatar_default);
        }

        setupStatusUI(holder, dto.getTrangThai());
    }

    private void setupStatusUI(HistoryViewHolder holder, int trangThai) {
        if (trangThai == 1) {
            holder.tvStatus.setText("Đúng giờ");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else {
            holder.tvStatus.setText("Đi muộn");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_red);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList != null ? attendanceList.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvStatus;
        ImageView imgAvatar;

        public HistoryViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvNameHistory);
            tvTime = v.findViewById(R.id.tvTimeHistory);
            tvStatus = v.findViewById(R.id.tvStatusHistory);
            imgAvatar = v.findViewById(R.id.imgAvatarHistory);
        }
    }
}