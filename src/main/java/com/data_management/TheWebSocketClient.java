package com.data_management;

import java.io.IOException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * TheWebSocketClient is a WebSocket client that connects to a server and reads data.
 * It implements the DataReader interface and uses a DataStorage instance to store received data.
 */
public class TheWebSocketClient extends WebSocketClient implements DataReader {
    private DataStorage dataStorage;

    /**
     * Constructs a new TheWebSocketClient with the specified server URI and DataStorage.
     *
     * @param serverUri    the URI of the server to connect to
     * @param dataStorage  the DataStorage instance to use for storing data
     */
    public TheWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    /**
     * Called when the WebSocket connection is opened.
     *
     * @param handshakedata  the server handshake data
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    /**
     * Called when a message is received from the server.
     *
     * @param message  the message received from the server
     */
    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        try {
            processMessage(message);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid message: " + e.getMessage());
        }
    }

    /**
     * Processes the received message and stores the data in the DataStorage.
     *
     * @param message  the message to process
     * @throws IllegalArgumentException if the message format is invalid
     */
    private void processMessage(String message) {
        String[] parts = message.split(",");
        validateMessageFormat(parts);

        int patientId = extractPatientId(parts[0]);
        long timestamp = extractTimestamp(parts[1]);
        String label = extractLabel(parts[2]);
        double data = extractData(parts[3]);

        dataStorage.addPatientData(patientId, data, label, timestamp);
    }

    /**
     * Validates the format of the message.
     *
     * @param parts  the parts of the message split by comma
     * @throws IllegalArgumentException if the message format is invalid
     */
    private void validateMessageFormat(String[] parts) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid message");
        }
    }

    /**
     * Extracts the patient ID from the message part.
     *
     * @param part  the message part containing the patient ID
     * @return the patient ID
     */
    private int extractPatientId(String part) {
        return Integer.parseInt(part.split(": ")[1]);
    }

    /**
     * Extracts the timestamp from the message part.
     *
     * @param part  the message part containing the timestamp
     * @return the timestamp in milliseconds since UNIX epoch
     */
    private long extractTimestamp(String part) {
        return Long.parseLong(part.split(": ")[1]);
    }

    /**
     * Extracts the label from the message part.
     *
     * @param part  the message part containing the label
     * @return the label
     */
    private String extractLabel(String part) {
        return part.split(": ")[1];
    }

    /**
     * Extracts the data from the message part.
     *
     * @param part  the message part containing the data
     * @return the data as a double
     */
    private double extractData(String part) {
        String dataStr = part.split(": ")[1];
        if (dataStr.contains("%")) {
            dataStr = dataStr.replace("%", "");
        }
        return Double.parseDouble(dataStr);
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code     the closure code
     * @param reason   the reason for closure
     * @param remote   whether the closure was initiated by the remote host
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with exit code " + code + " additional info: " + reason);
    }

    /**
     * Called when an error occurs.
     *
     * @param ex  the exception representing the error
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred: " + ex.getMessage());
        onClose(1001, "Error: " + ex.getMessage(), false); // Trigger onClose on error
    }

    /**
     * Reads data from the server and stores it in the specified DataStorage.
     *
     * @param dataStorage  the DataStorage instance to use for storing data
     * @param serverUri    the URI of the server to connect to
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void readData(DataStorage dataStorage, URI serverUri) throws IOException {
        this.dataStorage = dataStorage;
        connectToServer();
    }

    private void connectToServer() {
        try {
            this.connect();
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
