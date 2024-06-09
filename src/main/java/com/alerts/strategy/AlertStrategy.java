package com.alerts.strategy;


import com.data_management.Patient;

public interface AlertStrategy {
    
    public void checkAlert(Patient patient);
}
