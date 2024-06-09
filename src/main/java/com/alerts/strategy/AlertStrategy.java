package com.alerts.strategy;

import com.data_management.Patient;

/**
 * Interface for defining alert strategies.
 */
public interface AlertStrategy {
    
    /**
     * Checks for alerts based on a specific strategy.
     *
     * @param patient The patient for whom the alert is being checked.
     */
    public void checkAlert(Patient patient);
}
