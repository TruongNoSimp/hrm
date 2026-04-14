package com.example.hrm.listeners;

public interface OnItemActionListener<T> {
    void onEdit(T item);
    void onDelete(T item);
    void onItemClick(T item);
}