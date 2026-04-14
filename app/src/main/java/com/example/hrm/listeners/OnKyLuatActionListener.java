package com.example.hrm.listeners;

import com.example.hrm.models.Discipline;

public interface OnKyLuatActionListener {
    void onEdit(Discipline discipline);

    void onDelete(Discipline discipline);
}