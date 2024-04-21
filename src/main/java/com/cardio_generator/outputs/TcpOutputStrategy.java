package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * it is a strategy using to output the data unsig the tcp socket 
 * which It insures that information provided from one endpoint is appropriately received by another endpoint in the same sequence.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    /**
     * the contractorTcpOutputStrategy initiate serverSocket with the specified port.
     *
     * @param port the int port number is used to listen for client connections.
     * @throws IOException it check if I/O error occurs when opening the server socket.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * It Output the health data to the connected client using TCP socket.
     *
     * @param patientId The unique key for a spacific patient.
     * @param label Is the label that identify the type of health data.
     * @param timestamp Its the timestamp when the data was generated.
     * @param data      The data that is need to be output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
