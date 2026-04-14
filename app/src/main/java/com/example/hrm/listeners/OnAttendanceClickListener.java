package com.example.hrm.listeners;

import com.example.hrm.models.Employee;

public interface OnAttendanceClickListener {
    void onCheckInClick(Employee employee, String selectedTime);
}