package com.alerts.strategy;

import java.util.List;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * This class implements the AlertStrategy interface and generates alerts
 * based on heart rate readings from ECG records of a patient.
 */
public class HeartRateStrategy implements AlertStrategy {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
 

    @Override
    public void checkAlert(Patient patient) {
        long currentTime = System.currentTimeMillis();
        long oneHourAgo = currentTime - 3600000;
        List<PatientRecord> ecgRecords = dataStorage.getRecords(patient.getPatientId(), oneHourAgo, currentTime);

        for (PatientRecord record : ecgRecords) {
            if ("ECG".equals(record.getRecordType()) && (record.getMeasurementValue() < 50 || record.getMeasurementValue() > 100)) {
                alertGenerator.triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Abnormal Heart Rate Alert", record.getTimestamp()));
            }
        }
    }
}