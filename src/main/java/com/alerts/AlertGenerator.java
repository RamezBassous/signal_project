package com.alerts;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;


/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            throw new NullPointerException("Patient data is null.");
        }
    
        long currentTime = System.currentTimeMillis();
        long oneDayAgo = currentTime - 86400000;
        long oneHourAgo = currentTime - 3600000;
        long tenMinutesAgo = currentTime - 600000;
    
        evaluatePressure(getFilteredRecords(patient.getPatientId(), oneDayAgo, currentTime, "SystolicPressure"), "Systolic", currentTime, patient);
        evaluatePressure(getFilteredRecords(patient.getPatientId(), oneDayAgo, currentTime, "DiastolicPressure"), "Diastolic", currentTime, patient);
        evaluateBloodOxygen(patient);
        evaluateECG(getFilteredRecords(patient.getPatientId(), oneHourAgo, currentTime, "ECG"), patient);
        evaluateHypotensiveHypoxemia(getFilteredRecords(patient.getPatientId(), oneDayAgo, currentTime, "SystolicPressure"), getFilteredRecords(patient.getPatientId(), tenMinutesAgo, currentTime, "Saturation"), patient, currentTime);
    }
    
    private List<PatientRecord> getFilteredRecords(int patientId, long from, long to, String recordType) {
        return dataStorage.getRecords(patientId, from, to)
                .stream()
                .filter(r -> recordType.equals(r.getRecordType()))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    private void evaluateThreshold(List<PatientRecord> records, int threshold, String alertMessage, Patient patient) {
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < threshold) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), alertMessage, record.getTimestamp()));
                break;
            }
        }
    }
    
    private void evaluateThresholdAndTrend(List<PatientRecord> records, int upperThreshold, int lowerThreshold, int trendThreshold, String recordType, String thresholdAlert, String trendAlert, Patient patient, long currentTime) {
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() > upperThreshold || record.getMeasurementValue() < lowerThreshold) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), thresholdAlert + " (" + recordType + ")", record.getTimestamp()));
            }
        }
    
        if (records.size() >= 3) {
            boolean increasing = true;
            boolean decreasing = true;
            for (int i = 0; i < records.size() - 1; i++) {
                increasing &= (records.get(i).getMeasurementValue() - records.get(i + 1).getMeasurementValue() > trendThreshold);
                decreasing &= (records.get(i + 1).getMeasurementValue() - records.get(i).getMeasurementValue() > trendThreshold);
            }
    
            if (increasing) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), trendAlert + " Increasing Trend Alert", currentTime));
            }
            if (decreasing) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), trendAlert + " Decreasing Trend Alert", currentTime));
            }
        }
    }
    private void evaluatePressure(List<PatientRecord> records, String type, long currentTime, Patient patient) {
        if (type.equals("Systolic")) {
            // For systolic pressure, evaluate thresholds and trends with specific parameters
            evaluateThresholdAndTrend(records, 180, 90, 10, "SystolicPressure", "Critical Pressure Threshold Alert", type, patient, currentTime);
        } else if (type.equals("Diastolic")) {
            // For diastolic pressure, evaluate thresholds and trends with specific parameters
            evaluateThresholdAndTrend(records, 120, 60, 10, "DiastolicPressure", "Critical Pressure Threshold Alert", type, patient, currentTime);
        }
    }
    
    
    private void evaluateBloodOxygen(Patient patient) {
        long currentTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), currentTime - 600000, currentTime)
            .stream()
            .filter(r -> "Saturation".equals(r.getRecordType()))
            .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
            .collect(Collectors.toList());
    
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Low Saturation Alert", record.getTimestamp()));
                break; 
            }
        }
        for (int i = 1; i < records.size(); i++) {
            double previousValue = records.get(i - 1).getMeasurementValue();
            double currentValue = records.get(i).getMeasurementValue();
            double drop = previousValue - currentValue;
            if (drop >= 5) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Rapid Drop In BloodOxygen" , records.get(i).getTimestamp()));
                break;
            }
        }
    }
       
    
    private void evaluateECG(List<PatientRecord> records, Patient patient) {
        if (records.isEmpty()) {
            return;
        }
    
        evaluateThreshold(records, 50, "Abnormal Heart Rate Alert", patient);
    
        double averageInterval = calculateAverageInterval(records);
        double allowableVariation = averageInterval * 0.1;
    
        PatientRecord previousRecord = records.get(0);
        for (int i = 1; i < records.size(); i++) {
            PatientRecord currentRecord = records.get(i);
            long intervalDifference = Math.abs(currentRecord.getTimestamp() - previousRecord.getTimestamp());
    
            if (Math.abs(intervalDifference - averageInterval) > allowableVariation) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Irregular Beat Alert", currentRecord.getTimestamp()));
                break;
            }
            previousRecord = currentRecord;
        }
    }
    
    private void evaluateHypotensiveHypoxemia(List<PatientRecord> systolicRecords, List<PatientRecord> saturationRecords, Patient patient, long currentTime) {
        boolean low = systolicRecords.stream().anyMatch(r -> r.getMeasurementValue() < 90);
        boolean lowSaturation = saturationRecords.stream().anyMatch(r -> r.getMeasurementValue() < 92);
    
        if (low && lowSaturation) {
            triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Hypotensive Hypoxemia Alert", currentTime));
        }
    }
    
    private double calculateAverageInterval(List<PatientRecord> ecgRecords) {
        long totalInterval = 0;
        for (int i = 1; i < ecgRecords.size(); i++) {
            totalInterval += (ecgRecords.get(i).getTimestamp() - ecgRecords.get(i - 1).getTimestamp());
        }
        return totalInterval / (double) (ecgRecords.size() - 1);
    }
    
    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("Alert triggered for patient: " + alert.getPatientId() +
            ", Condition: " + alert.getCondition() +
            ", Timestamp: " + alert.getTimestamp());
    }

   
}
