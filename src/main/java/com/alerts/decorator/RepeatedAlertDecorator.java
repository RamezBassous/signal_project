package com.alerts.decorator;

public class RepeatedAlertDecorator extends AlertDecorator {
    @Override
    public void triggerAlert() {
        super.triggerAlert();
        System.out.println("The repeated alert is triggered.");
    }
}
