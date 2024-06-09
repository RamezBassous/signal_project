package data_management;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.TheWebSocketClient;

public class IntegrationTests {

    private DataStorage storage;
    private AlertGenerator alertGen;
    private TheWebSocketClient wsClient;
    private ByteArrayOutputStream outputCapture;
    private PrintStream originalOutput;
    private long startTime;

    // Set up before each test
    @BeforeEach
    void setup() throws URISyntaxException {
        storage = new DataStorage();
        alertGen = spy(new AlertGenerator(storage));
        wsClient = spy(new TheWebSocketClient(new URI("ws://localhost:8080"), storage));
        outputCapture = new ByteArrayOutputStream();
        originalOutput = System.out;
        System.setOut(new PrintStream(outputCapture));
        startTime = System.currentTimeMillis();
    }

    // Clean up after each test
    @AfterEach
    void teardown() {
        System.setOut(originalOutput);
    }

    // Test the integration of components
    @Test
    public void testIntegration() throws Exception {
        doNothing().when(wsClient).connect();
        wsClient.onOpen(mock(ServerHandshake.class));

        long timestamp = startTime - 10000;
        String message = "Patient ID: 19, Timestamp: " + timestamp + ", Label: Saturation, Data: 90";
        wsClient.onMessage(message);

        List<Patient> patients = storage.getAllPatients();
        assertFalse(patients.isEmpty());
        assertEquals(19, patients.get(0).getPatientId());

        List<PatientRecord> records = storage.getRecords(19, timestamp, timestamp);
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertEquals("Saturation", records.get(0).getRecordType());
        assertEquals(90, records.get(0).getMeasurementValue(), 0.001);

        alertGen.evaluateData(patients.get(0));// Evaluate data for the first patient to check for alerts
        assertTrue(outputCapture.toString().contains("Low Saturation Alert"));
    }

    // Test successful connection during data reading
    @Test
    public void testSuccessfulConnectionDuringRead() throws URISyntaxException, IOException {
        TheWebSocketClient spyReader = spy(wsClient);

        doNothing().when(spyReader).connect();
        spyReader.readData(storage, new URI("ws://localhost:8080"));

        verify(spyReader, times(1)).connect();// Verify that the connect method was called exactly once
    }


}
