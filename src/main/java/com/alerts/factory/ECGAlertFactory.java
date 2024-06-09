package com.alerts.factory;

import com.alerts.Alert;

/**
 * Factory for creating ECGAlert objects.
 */
public class ECGAlertFactory extends AlertFactory {

    /**
     * Creates an ECG alert with the given details.
     *
     * @param patientId ID of the patient
     * @param condition Condition triggering the alert
     * @param timestamp Time of the alert
     * @return Created ECG Alert object
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        Alert alert = new Alert(patientId, condition, timestamp);
        return alert;
    }
}
