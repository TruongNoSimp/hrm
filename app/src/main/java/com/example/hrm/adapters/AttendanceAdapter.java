package com.example.hrm.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrm.R;
import com.example.hrm.dto.EmployeeAttendanceDTO;
import com.example.hrm.listeners.OnAttendanceClickListener;
import com.example.hrm.models.Employee;
import com.example.hrm.utils.DateUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private Context context;
    private List<EmployeeAttendanceDTO> employeeList;
    private OnAttendanceClickListener listener;


    public AttendanceAdapter(Context context, List<EmployeeAttendanceDTO> employeeList, OnAttendanceClickListener listener) {
        this.context = context;
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        EmployeeAttendanceDTO dto = employeeList.get(position);

        Employee emp = dto.getEmployee();

        holder.tvName.setText(emp.getHoTen());
        holder.tvInfo.setText("Mã NV: " + emp.getMaNv());

        holder.tvDepartment.setText("Phòng: " + dto.getTenPhongBan());

        if (emp.getAvatar() != null && !emp.getAvatar().isEmpty()) {
            holder.imgAvatar.setImageURI(Uri.parse(emp.getAvatar()));
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_avatar_default);
        }

        SharedPreferences prefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String workShift = prefs.getString("work_shift", "08:00") + ":00";

        if (dto.getGioVao() != null && !dto.getGioVao().isEmpty()) {
            String displayTime = DateUtils.formatDisplayTime(context, dto.getGioVao());
            holder.tvStatus.setText("Vào lúc: " + displayTime);
            int color = dto.getTrangThaiChamCong() == 1 ? Color.GREEN : Color.RED;
            holder.tvStatus.setTextColor(color);
            holder.btnCheckIn.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("Chưa điểm danh");
            holder.tvStatus.setTextColor(Color.GRAY);
            holder.btnCheckIn.setVisibility(View.VISIBLE);
        }

        holder.btnCheckIn.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(context, (view, hour, minute) -> {
                String timeForDb = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);

                if (listener != null) {
                    listener.onCheckInClick(emp, timeForDb);

                    String timeForDisplay = DateUtils.formatDisplayTime(context, timeForDb);
                    holder.tvStatus.setText("Vào lúc: " + timeForDisplay);

                    int color = timeForDb.compareTo(workShift) > 0 ? Color.RED : Color.GREEN;
                    holder.tvStatus.setTextColor(color);
                    holder.btnCheckIn.setVisibility(View.GONE);
                }
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    @Override
    public int getItemCount() {
        return employeeList != null ? employeeList.size() : 0;
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvInfo, tvStatus, tvDepartment;
        Button btnCheckIn;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatarAttendance);
            tvName = itemView.findViewById(R.id.tvNameAttendance);
            tvInfo = itemView.findViewById(R.id.tvRoleAttendance);
            tvStatus = itemView.findViewById(R.id.tvStatusAttendance);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
        }
    }
}
