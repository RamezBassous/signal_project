package com.alerts.strategy;

import java.util.List;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Strategy for generating alerts based on heart rate readings from ECG records.
 */
public class HeartRateStrategy implements AlertStrategy {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    /**
     * Checks for abnormal heart rate alerts for the given patient.
     *
     * @param patient The patient for whom the heart rate alerts are checked.
     */
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
