package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;

public class TestFactoryMethod {

    /*
        Test for creating a BloodOxygenAlert using BloodOxygenAlertFactory.
    */
    @Test
    public void testAlertFactoryBloodOxygen() {
        testFactory(new BloodOxygenAlertFactory(), "12", "low oxygen", 2632283355000L);
    }
    
    /*
        Test for creating an ECGAlert using ECGAlertFactory.
    */
    @Test
    public void testAlertFactoryECG() {
        testFactory(new ECGAlertFactory(), "14", "abnormal ECG", 2632283355000L);
    }

    /*
        Test for creating a BloodPressureAlert using BloodPressureAlertFactory.
    */
    @Test
    public void testAlertFactoryBloodPressure() {
        testFactory(new BloodPressureAlertFactory(), "13", "high blood pressure", 2632283355000L);
    }

    /*
        Generic method to test the creation of alerts.
    */
    private void testFactory(AlertFactory factory, String patientId, String condition, long timestamp) {
        Alert alert = factory.createAlert(patientId, condition, timestamp);
        assertNotNull(alert);
        assertEquals(patientId, alert.getPatientId());
        assertEquals(condition, alert.getCondition());
        assertEquals(timestamp, alert.getTimestamp());
    }
}