package com.alerts.decorator;

/**
 * Adds repeated alert handling.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    
    /**
     * Triggers the alert and adds repeated alert handling.
     */
    @Override
    public void triggerAlert() {
        super.triggerAlert();
        System.out.println("The repeated alert is triggered.");
    }
}
