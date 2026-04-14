package com.example.hrm.listeners;

import com.example.hrm.models.Employee;

public interface OnEmployeeActionListener {
    void onEdit(Employee employee);

    void onDelete(Employee employee);

    void onItemClick(Employee employee);
}