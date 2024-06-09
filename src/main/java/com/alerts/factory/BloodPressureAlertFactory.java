package com.alerts.factory;

import com.alerts.Alert;

public class BloodPressureAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {

        Alert alert = new Alert(patientId, condition, timestamp);
        
        return alert;
    }
}
