package com.alerts.factory;

import com.alerts.Alert;

/**
 * Abstract factory for creating Alert objects.
 */
public abstract class AlertFactory {

    /**
     * Creates an alert with the given details.
     *
     * @param patientId ID of the patient
     * @param condition Condition triggering the alert
     * @param timestamp Time of the alert
     * @return Created Alert object
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);

}
