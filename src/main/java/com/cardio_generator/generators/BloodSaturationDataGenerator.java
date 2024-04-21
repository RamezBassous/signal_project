package com.cardio_generator.generators;

import java.io.IOException;
import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This class generate a simulation of blood saturation data for patients.
 */

public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

     /**
     * the contracture using the patientCount it initialize with baseline saturation values for each patient
     * 
     * @param patientCount The number of patients that the data will be generated for.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }
    /**
     * The method Generates blood saturation data fo specific patient and ensure the saturation stays within a realistic and healthy range.
     *
     * @param patientId     is the key for spacific patient.
     * @param outputStrategy The strategy for outputting the generated data which is imported form othe class in the same package.
     * @throws IOException it check if I/O error occurs and print the stack trace to help identify where the error occurred
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
