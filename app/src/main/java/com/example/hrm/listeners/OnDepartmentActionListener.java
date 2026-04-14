package com.example.hrm.listeners;

import com.example.hrm.models.Department;

public interface OnDepartmentActionListener {
        void onEdit(Department department);
        void onDelete(Department department);
        void onItemClick(Department department);
    }