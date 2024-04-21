package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * it is an interface that generate data for spacific patient 
 */
public interface PatientDataGenerator {
    /**
     * it Generates data for a specific patient.
     *
     * @param patientId is the key for spacific patient.
     * 
     * @param outputStrategy it is the strategy for generated data to outputted.
     *                       
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
