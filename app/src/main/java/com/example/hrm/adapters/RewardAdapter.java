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
import com.example.hrm.listeners.OnItemActionListener;
import com.example.hrm.models.Reward;
import com.example.hrm.utils.DateUtils;

import java.text.DecimalFormat;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.KhenThuongViewHolder> {

    private List<Reward> rewardList;
    private OnItemActionListener<Reward> listener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final Context context;

    public RewardAdapter(Context context, List<Reward> rewardList, OnItemActionListener<Reward> listener) {
        this.context = context;
        this.rewardList = rewardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KhenThuongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
        return new KhenThuongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhenThuongViewHolder holder, int position) {
        Reward reward = rewardList.get(position);

        holder.tvTenNhanVien.setText(reward.getTenNhanVien());
        holder.tvMaNhanVien.setText(reward.getMaNhanVien());

        String displayDate = DateUtils.formatDisplayDate(context, reward.getNgayQuyetDinh());
        holder.tvNgayQuyetDinh.setText(displayDate);

        holder.tvHinhThuc.setText(reward.getHinhThuc());
        holder.tvSoTienThuong.setText("+" + decimalFormat.format(reward.getSoTienThuong()) + " VNĐ");
        holder.tvLyDo.setText("Lý do: " + reward.getLyDo());

        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(reward);
            }
        });

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(reward);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardList != null ? rewardList.size() : 0;
    }

    public void setData(List<Reward> newList) {
        this.rewardList = newList;
        notifyDataSetChanged();
    }

    public static class KhenThuongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNhanVien, tvMaNhanVien, tvNgayQuyetDinh, tvHinhThuc, tvSoTienThuong, tvLyDo;
        ImageView imgEdit, imgDelete;

        public KhenThuongViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenNhanVien = itemView.findViewById(R.id.tvTenNhanVien);
            tvMaNhanVien = itemView.findViewById(R.id.tvMaNhanVien);
            tvNgayQuyetDinh = itemView.findViewById(R.id.tvNgayQuyetDinh);
            tvHinhThuc = itemView.findViewById(R.id.tvHinhThuc);
            tvSoTienThuong = itemView.findViewById(R.id.tvSoTienThuong);
            tvLyDo = itemView.findViewById(R.id.tvLyDo);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}