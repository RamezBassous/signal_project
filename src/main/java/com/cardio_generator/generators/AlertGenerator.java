package com.cardio_generator.generators;

import java.io.IOException;
import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;
/**
 * this class generate a simulating alerts for patients.
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // changed the first letter in AlertStates to lower case as the variable name first letter need to be in lower Case
    private boolean[] alertStates; // false = resolved, true = pressed
    //as CHANCE_TO_RESOLVE are Constant it is declared static final fields whose contents are deeply immutable and whose methods have no detectable side effects
    private static final double CHANCE_TO_RESOLVE = 0.9;  
    
    
    /**
     * AlertGenerator construct an alertstates Boolean array for a given number of patients
     * 
     * @param patientCount gives the number of patients that is implemented to choose the size of the array 
     */
    public AlertGenerator(int patientCount) {
    // changed the first letter in AlertStates to lower case 
        alertStates = new boolean[patientCount + 1];
    }
    /**
     * simulates the alert data for a patient, 
     * by triggering new alerts based on estimated probabilities or resolving current ones with a 90% probability.  
     *
     * @param patientId     is the key for spacific patient.
     * @param outputStrategy The strategy for outputting the generated data which is imported form othe class in the same package.
     * 
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) { // changed the first letter in AlertStates to lower case 
                // changed 0.9 as named to a constant name
                if (randomGenerator.nextDouble() < CHANCE_TO_RESOLVE) { // 90% chance to resolve
                    alertStates[patientId] = false; // changed the first letter in AlertStates to lower case 
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                
                // Average rate (alerts per period), adjust based on desired frequency
                // Use lowerCamelCase for variable names as it is no a constant variable
                double lambda = 0.1;
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;   // changed the first letter in AlertStates to lower case 

                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
