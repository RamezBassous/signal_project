package com.alerts.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Strategy for checking blood pressure alerts.
 */
public class BloodPressureStrategy implements AlertStrategy {

    public DataStorage dataStorage;
    public AlertGenerator alertGenerator;

    /**
     * Checks for blood pressure alerts for the given patient.
     *
     * @param patient The patient for whom the blood pressure alerts are checked.
     */
    @Override
    public void checkAlert(Patient patient) {
        long currentTime = System.currentTimeMillis();
        long oneDayAgo = currentTime - 86400000;

        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), oneDayAgo, currentTime);

        List<PatientRecord> systolicRecords = records.stream()
                .filter(r -> "SystolicPressure".equals(r.getRecordType()))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp).reversed())
                .collect(Collectors.toList());

        List<PatientRecord> diastolicRecords = records.stream()
                .filter(r -> "DiastolicPressure".equals(r.getRecordType()))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp).reversed())
                .collect(Collectors.toList());

        checkPressureAlerts(systolicRecords, "Systolic", patient);
        checkPressureAlerts(diastolicRecords, "Diastolic", patient);
        checkTrendAndTriggerAlert(systolicRecords, "Systolic", patient, currentTime);
        checkTrendAndTriggerAlert(diastolicRecords, "Diastolic", patient, currentTime);
    }

    /**
     * Checks for high or low blood pressure alerts.
     *
     * @param records List of patient records
     * @param type    Type of pressure (Systolic or Diastolic)
     * @param patient The patient for whom the alerts are checked.
     */
    public void checkPressureAlerts(List<PatientRecord> records, String type, Patient patient) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            if (("Systolic".equals(type) && (value > 180 || value < 90)) ||
                    ("Diastolic".equals(type) && (value > 120 || value < 60))) {
                alertGenerator.triggerAlert(new Alert(Integer.toString(patient.getPatientId()), type + " pressure alert", record.getTimestamp()));
            }
        }
    }

    /**
     * Checks for trends in blood pressure and triggers alerts accordingly.
     *
     * @param records     List of patient records
     * @param type        Type of pressure (Systolic or Diastolic)
     * @param patient     The patient for whom the alerts are checked.
     * @param currentTime The current time
     */
    public void checkTrendAndTriggerAlert(List<PatientRecord> records, String type, Patient patient, long currentTime) {
        if (records.size() >= 3) {
            boolean increasing = true;
            boolean decreasing = true;
            for (int i = 0; i < records.size() - 1; i++) {
                increasing &= (records.get(i).getMeasurementValue() - records.get(i + 1).getMeasurementValue() > 10);
                decreasing &= (records.get(i + 1).getMeasurementValue() - records.get(i).getMeasurementValue() > 10);
            }

            if (increasing) {
                alertGenerator.triggerAlert(new Alert(Integer.toString(patient.getPatientId()), type + " Pressure Increasing", currentTime));
            }
            if (decreasing) {
                alertGenerator.triggerAlert(new Alert(Integer.toString(patient.getPatientId()), type + " Pressure Decreasing", currentTime));
            }
        }
    }
}
