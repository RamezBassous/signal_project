package com.alerts.decorator;

/**
 * Adds priority handling to alerts.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    
    /**
     * Triggers the alert and adds priority handling.
     */
    @Override
    public void triggerAlert() {
        super.triggerAlert();
        System.out.println("The Priority alert is triggered.");
    }
}
