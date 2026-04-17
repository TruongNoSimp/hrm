package com.example.hrm.listeners;

import com.example.hrm.dto.TrainingDTO;

public interface OnTrainingActionListener {
    void onEdit(TrainingDTO dto);

    void onDelete(TrainingDTO dto);
}