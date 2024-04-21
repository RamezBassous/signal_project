package com.cardio_generator.outputs;
/**
 * It is an interface that output generated data in spacific order (strategy)
 */
public interface OutputStrategy {

     /**
     * it Outputs generated health data for a specific patient.
     *
     * @param patientId The unique key for a spacific patient.
     * @param timestamp Its the timestamp when the data was generated.
     * @param label     Is the label that identify the type of health data such as "ECG", "Blood Pressure", etc.
     * @param data      The data that is need to be output.
     */

    void output(int patientId, long timestamp, String label, String data);
}
