package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 * It is a strategy that outpute the health generated data to files.
 */

// the class name should start with a UpperCamelCase so rename from fileOutputStrategy to FileOutputStrategy
public class FileOutputStrategy implements OutputStrategy {
// In String name the first letter should be a small letter so from BaseDirectory to baseDirectory
    private String baseDirectory;

    public final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();

    
 /**
     * the constructor initiate baseDirectory with specified base directory.
     *
     * @param baseDirectory The base directory where output files will be stored.
     */

    // change as will the Constructor name as the class name

    public FileOutputStrategy(String baseDirectory) {

    // change the string name as well to the same as the private String baseDirectory for this.BaseDirectory to this.baseDirectory 
        this.baseDirectory = baseDirectory;
    }
    /**
     * Outputs the health data to a specifice file.
     *
     * @param patientId The unique key for a spacific patient.
     * @param label Is the label that identify the type of health data.
     * @param timestamp Its the timestamp when the data was generated.
     * @param data      The data that is need to be output.
     * 
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));// change the name of string as well to the same as the private String baseDirectory form BaseDirectory to baseDirectory 
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // change the name of string as well to the same as the private String baseDirectory form BaseDirectory to baseDirectory 
        // as will for the  String FilePath should the first letter to smallcase from FilePath to filePath
        // Set the FilePath variable 
        String filePath = file_map.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
        // as will for the FilePath should the first letter to smallcase from FilePath to filePath    
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}