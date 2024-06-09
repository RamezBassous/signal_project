package com.alerts.factory;

import com.alerts.Alert;

public class ECGAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {

        Alert alert = new Alert(patientId, condition, timestamp);
        
        return alert;
    }
}
