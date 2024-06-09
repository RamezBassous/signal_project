package com.alerts.factory;

import com.alerts.Alert;

/**
 * Factory for creating BloodOxygenAlert objects.
 */
public class BloodOxygenAlertFactory extends AlertFactory {

    /**
     * Creates a Blood Oxygen alert with the given details.
     *
     * @param patientId ID of the patient
     * @param condition Condition triggering the alert
     * @param timestamp Time of the alert
     * @return Created Blood Oxygen Alert object
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        Alert alert = new Alert(patientId, condition, timestamp);
        return alert;
    }
}
