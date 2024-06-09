package com.alerts.decorator;

import com.alerts.Alert;
import com.alerts.AlertGenerator;

/**
 * Abstract class for adding responsibilities to AlertGenerator.
 */
public abstract class AlertDecorator {
    // Alert generator instance
    private AlertGenerator alertGenerator;

    // Alert details
    protected Alert alert;

    /**
     * Triggers an alert. Can be extended to add more behaviors.
     *
     * Assumes:
     * 1. Alert is fully formed.
     * 2. AlertGenerator is initialized.
     */
    public void triggerAlert() {
        alertGenerator.triggerAlert(alert);
    }
}
