package com.alerts.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

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

    private void checkPressureAlerts(List<PatientRecord> records, String type, Patient patient) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            if (("Systolic".equals(type) && (value > 180 || value < 90)) ||
                ("Diastolic".equals(type) && (value > 120 || value < 60))) {
                alertGenerator.triggerAlert(new Alert(Integer.toString(patient.getPatientId()), type + " pressure alert", record.getTimestamp()));
            }
        }
    }

    private void checkTrendAndTriggerAlert(List<PatientRecord> records, String type, Patient patient, long currentTime) {
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
