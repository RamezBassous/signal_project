package com.alerts.strategy;

import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @Override
    public void checkAlert(Patient patient) {
        long currentTime = System.currentTimeMillis();
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), currentTime - 600000, currentTime)
            .stream()
            .filter(r -> "Saturation".equals(r.getRecordType()))
            .sorted((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()))
            .collect(Collectors.toList());

        if (records.isEmpty()) return;

        PatientRecord previousRecord = null;

        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            // Check for low saturation
            if (value < 92) {
                alertGenerator.triggerAlert(new Alert(
                    Integer.toString(patient.getPatientId()), 
                    "Low Saturation Alert", 
                    record.getTimestamp()
                ));
                return;
            }

            // Check for rapid drop in blood oxygen levels
            if (previousRecord != null) {
                double dropPercentage = 100.0 * (previousRecord.getMeasurementValue() - value) / previousRecord.getMeasurementValue();
                if (dropPercentage >= 5) {
                    alertGenerator.triggerAlert(new Alert(
                        Integer.toString(patient.getPatientId()), 
                        "Rapid Blood Oxygen Drop Alert", 
                        record.getTimestamp()
                    ));
                    return;
                }
            }
            previousRecord = record;
        }
    }
}


