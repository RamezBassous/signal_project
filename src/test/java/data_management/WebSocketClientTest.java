package data_management;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.handshake.ServerHandshake;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.data_management.DataStorage;
import com.data_management.TheWebSocketClient;

public class WebSocketClientTest {
    private TheWebSocketClient client;
    private DataStorage mockStorage;

    @Before
    public void setUp() throws URISyntaxException {
        mockStorage = mock(DataStorage.class);
        client = new TheWebSocketClient(new URI("ws://localhost:8080"), mockStorage);
    }

    @Test
    public void testOnOpen() {
        // Capture system output for verification
        try (ByteArrayOutputStream outContent = new ByteArrayOutputStream();
             PrintStream originalOut = System.out;
             PrintStream printStream = new PrintStream(outContent)) {

            System.setOut(printStream);

            // Mock the server handshake
            ServerHandshake handshake = mock(ServerHandshake.class);
            client.onOpen(handshake);

            System.setOut(originalOut);
            // Verify the output
            assertTrue(outContent.toString().contains("Connected to server"));
        } catch (IOException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    public void testOnMessageValid() {
        // Test with a valid message
        String message = "Patient ID: 25, Timestamp: 22127484682232, Label: ECG, Data: -0.22423456666854234";
        client.onMessage(message);
        verify(mockStorage).addPatientData(25, -0.22423456666854234, "ECG", 22127484682232L);
    }

    @Test
    public void testOnMessageInvalid() {
        // Test with an invalid message
        String message = "Invalid message";
        client.onMessage(message);
        verify(mockStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    public void parsingErrorOnMessagetest() {
        // Test with a message containing a parsing error
        String message = "Patient ID: not_number, Timestamp: 22127484682232, Label: M, Data: 12233.789";
        client.onMessage(message);
        verify(mockStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }


    @Test
    public void testOnMessage_unexpectedError() {
        TheWebSocketClient spyClient = spy(client);
    
        doThrow(new RuntimeException("Unexpected error")).when(mockStorage).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    
        try {
            String message = "Patient ID: 20, Timestamp: 22127484682232, Label: EEG, Data: 0.23445563464532";

            spyClient.onMessage(message);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            assertEquals("Unexpected error", e.getMessage());
        }
    }


    @Test
    public void testOnClose() {
        // Test onClose method
        client.onClose(1000, "Normal closure", false);
        assertFalse(client.isOpen());
    }
    
    @Test
    public void testOnCloseWithException() {
        // Test onClose method with an exception
        TheWebSocketClient spyClient = spy(client);
    
        doThrow(new RuntimeException("Reconnection failed")).when(spyClient).onClose(anyInt(), anyString(), anyBoolean());
    
        assertThrows(RuntimeException.class, () -> {
            spyClient.onClose(1006, "Abnormal closure", true);
        }, "Expected RuntimeException was not thrown");
    
        assertFalse(spyClient.isOpen());
    }
    
    @Test
    public void testOnError() throws InterruptedException {
        // Test onError method
        TheWebSocketClient spyClient = spy(client);
        CountDownLatch latch = new CountDownLatch(1);
    
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(spyClient).onClose(anyInt(), anyString(), anyBoolean());
    
        try (ByteArrayOutputStream errContent = new ByteArrayOutputStream();
             PrintStream originalErr = System.err;
             PrintStream printStream = new PrintStream(errContent)) {
    
            System.setErr(printStream);
            spyClient.onError(new Exception("Test exception"));
    
            boolean onCloseCalled = latch.await(5, TimeUnit.SECONDS);
            System.setErr(originalErr);
    
            assertTrue("onClose method is called", onCloseCalled);
            assertTrue(errContent.toString().contains("Test exception"));
        } catch (IOException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }
    
    @Test
    public void testSuccessfulConnectionForReadData() throws URISyntaxException, IOException {
        // Test successful connection when reading data
        TheWebSocketClient spyClient = spy(client);
        doNothing().when(spyClient).connect();
        spyClient.readData(mockStorage, new URI("ws://localhost:8080"));
        verify(spyClient, times(1)).connect();
    }
    
    @Test
    public void ReadDataAttemptToConnectFailure() throws URISyntaxException, IOException {
        // Test failure to connect when reading data
        TheWebSocketClient spyClient = spy(client);
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        PrintStream printStream = new PrintStream(errContent);
        
        System.setErr(printStream);
    
        doThrow(new RuntimeException("Connection failed")).when(spyClient).connect();
        spyClient.readData(mockStorage, new URI("ws://localhost:8080"));
    
        System.setErr(originalErr);
        assertTrue(errContent.toString().contains("Connection failed"));
    }
}