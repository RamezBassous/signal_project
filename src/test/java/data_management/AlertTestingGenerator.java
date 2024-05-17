package data_management;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

class AlertTestingGenerator {

    private DataStorage mockDataStorage;
    private AlertGenerator alertGenerator;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private long currentTime;
    
    @BeforeEach
    void initialize() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        mockDataStorage = Mockito.mock(DataStorage.class);
        alertGenerator = new AlertGenerator(mockDataStorage);
        currentTime = System.currentTimeMillis();
        originalOut = System.out;
    }
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testEvaluateSystolicPressureCriticalAlert() {
        // Create a patient
        Patient patient = new Patient(1);
        
        // Define current time
        long currentTime = System.currentTimeMillis();
    
        // Create a systolic record
        List<PatientRecord> systolicRecord = Arrays.asList(
            new PatientRecord(1, 200, "SystolicPressure", currentTime)  
        );
    
        // Mock the data storage to return the systolic record
        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(systolicRecord);
    
        // Invoke the method under test
        alertGenerator.evaluateData(patient);
    
        // Capture the output
        String output = outContent.toString();
    
        // Assertion
        assertTrue(output.contains("SystolicPressure"), 
            "Expected 'SystolicPressure' but got: " + output);
    }
    
    @Test
void testEvaluateDiastolicPressureCriticalAlert() {
    // Create a patient
    Patient patient = new Patient(1);
    
    // Define current time
    long currentTime = System.currentTimeMillis();

    // Create a diastolic record
    List<PatientRecord> diastolicRecord = Arrays.asList(
        new PatientRecord(1, 10, "DiastolicPressure", currentTime)  
    );

    // Mock the data storage to return the diastolic record
    Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(diastolicRecord);

    // Invoke the method under test
    alertGenerator.evaluateData(patient);

    // Capture the output
    String output = outContent.toString();

    // Assertion
    assertTrue(output.contains("DiastolicPressure"), 
        "Expected result'DiastolicPressure' but got: " + output);
}


    @Test
    void testEvaluateSaturationLowAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> saturationRecord = Arrays.asList(
            new PatientRecord(1, 85, "Saturation", currentTime)  
        );
        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(saturationRecord);
        alertGenerator.evaluateData(patient);
        String output = outContent.toString();
        assertTrue(output.contains("Low Saturation Alert"), 
            "Expected result 'Low Saturation Alert' but got: " + output);
    }

    @Test
    void testEvaluateSaturationRapidDropAlert() {
        Patient patient = new Patient(1);
        long currentTime = System.currentTimeMillis();
        List<PatientRecord> saturationRecords = Arrays.asList(
            new PatientRecord(1, 100, "Saturation", currentTime - 1000),
            new PatientRecord(1, 94, "Saturation", currentTime)
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(saturationRecords);

        alertGenerator.evaluateData(patient);

        String output = outContent.toString().trim();  // Capture the output

        assertTrue(output.contains("Rapid Drop In BloodOxygen"), 
            "Expected result'Rapid Drop In BloodOxygen' but got: " + output);
    }
    
    @Test
    void testEvaluateECGAbnormalHeartRateAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> ecgRecord = Arrays.asList(
            new PatientRecord(1, 40, "ECG", currentTime)  
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(ecgRecord);

        alertGenerator.evaluateData(patient);

        String output = outContent.toString();
        assertTrue(output.contains("Abnormal Heart Rate Alert"), 
            "Expected 'Abnormal Heart Rate Alert' for low heart rate but got: " + output);
    }

    @Test
    void testEvaluateECGIrregularBeatAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> ecgRecord = Arrays.asList(
            new PatientRecord(1, 90, "ECG", currentTime - 1000),
            new PatientRecord(1, 80, "ECG", currentTime)  
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(ecgRecord);

        alertGenerator.evaluateData(patient);

        String output = outContent.toString();
        assertTrue(output.contains("Irregular Beat Alert"), 
            "Expected 'Irregular Beat Alert' but got: " + output);
    }

    @Test
    void testHypotensiveHypoxemiaAlertGeneration() {
       
        int patientId = 1;
        List<PatientRecord> records = Arrays.asList(
            new PatientRecord(patientId, 89, "SystolicPressure", currentTime),
            new PatientRecord(patientId, 91, "Saturation", currentTime)
        );
        Mockito.when(mockDataStorage.getRecords(Mockito.eq(patientId), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(records);

        alertGenerator.evaluateData(new Patient(patientId));
        assertTrue(outContent.toString().contains("Hypotensive Hypoxemia Alert"));
    }

    @Test
    void testEvaluateIncreasingTrendAlert() {
        long currentTime = System.currentTimeMillis();
        DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
        AlertGenerator alertGenerator = new AlertGenerator(mockDataStorage);
        Patient patient = new Patient(1);
        List<PatientRecord> systolicRecords = Arrays.asList(
            new PatientRecord(1, 120, "SystolicPressure", currentTime - 3000),  // First reading
            new PatientRecord(1, 130, "SystolicPressure", currentTime - 2000),  // Second reading
            new PatientRecord(1, 140, "SystolicPressure", currentTime - 1000)   // Third reading
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(systolicRecords);
        alertGenerator.evaluateData(patient);

        String output = outContent.toString();
        assertTrue(output.contains("Trend Alert: Increasing Trend"), 
            "Expected 'Trend Alert: Increasing Trend' for increasing systolic pressure but got: " + output);
    }
    @Test
    void testEvaluateDecreasingTrendAlert() {
        long currentTime = System.currentTimeMillis();
        DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
        AlertGenerator alertGenerator = new AlertGenerator(mockDataStorage);
        Patient patient = new Patient(1);
        List<PatientRecord> systolicRecords = Arrays.asList(
        new PatientRecord(1, 140, "SystolicPressure", currentTime - 3000),  // First reading
        new PatientRecord(1, 130, "SystolicPressure", currentTime - 2000),  // Second reading
        new PatientRecord(1, 120, "SystolicPressure", currentTime - 1000)   // Third reading
        );

        Mockito.when(mockDataStorage.getRecords(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(systolicRecords);
         alertGenerator.evaluateData(patient);

        // Verify that the alert message is triggered
        String output = outContent.toString();
        assertTrue(output.contains("Trend Alert: Decreasing Trend"), 
        "Expected 'Trend Alert: Decreasing Trend' for decreasing systolic pressure but got: " + output);
}
}
