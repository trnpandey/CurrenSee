package com.tarun.currensee;

public interface DenominationsChange {
    void addMoney(int denomination);
    void deductMoney(int denomination);
    void updateViews();
    void saveToDatabase();
}
