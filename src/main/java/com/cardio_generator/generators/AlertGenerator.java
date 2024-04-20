package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // changed the first letter in AlertStates to lower case as the variable name first letter need to be in lower Case
    private boolean[] alertStates; // false = resolved, true = pressed
    //as CHANCE_TO_RESOLVE are Constant it is declared static final fields whose contents are deeply immutable and whose methods have no detectable side effects
    private static final double CHANCE_TO_RESOLVE = 0.9;  
    
    
    
    public AlertGenerator(int patientCount) {
    // changed the first letter in AlertStates to lower case 
        alertStates = new boolean[patientCount + 1];
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) { // changed the first letter in AlertStates to lower case 
                // changed 0.9 as named a constant 
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
