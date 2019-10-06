package hu.gyengus.thermometerservice.thermometer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import hu.gyengus.thermometerservice.Observer;
import hu.gyengus.thermometerservice.TemperatureObserver;
import hu.gyengus.thermometerservice.TemperatureSubject;
import hu.gyengus.thermometerservice.serial.SerialPortClient;

class ArduinoThermometerTest {

    private static final String READTEMP_COMMAND = "READTEMP\n";

    @Mock
    private SerialPortClient serialPortClient;

    private Thermometer underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new ArduinoThermometer(serialPortClient);
    }

    @Test
    void testIsConnectedShouldReturnTrueWhenSerialPortIsOpen() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(true);
        // WHEN
        boolean actual = underTest.isConnected();
        // THEN
        assertTrue(actual);
    }

    @Test
    void testIsConnectedShouldReturnFalseWhenSerialPortIsClosed() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(false);
        // WHEN
        boolean actual = underTest.isConnected();
        // THEN
        assertFalse(actual);
    }

    @Test
    void testSendReadCommandWhenSerialPortIsOpen() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(true);
        BDDMockito.doNothing().when(serialPortClient).write(READTEMP_COMMAND);
        // WHEN
        underTest.sendReadCommand();
        // THEN
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).write(READTEMP_COMMAND);
    }

    @Test
    void testSendReadCommandWhenSerialPortIsClosed() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(false);
        BDDMockito.given(serialPortClient.open()).willReturn(true);
        BDDMockito.doNothing().when(serialPortClient).write(READTEMP_COMMAND);
        // WHEN
        underTest.sendReadCommand();
        // THEN
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).write(READTEMP_COMMAND);
    }

    @Test
    void testUpdateShouldThrowExceptionWhenGotError() {
        // GIVEN
        TemperatureObserver observer = Mockito.mock(TemperatureObserver.class);
        BDDMockito.doNothing().when(observer).update(null);
        ((TemperatureSubject) underTest).setObserver(observer);
        // WHEN
        Executable callUpdate = () -> ((Observer) underTest).update("ERROR: Something went wrong");
        // THEN
        ThermometerException e = assertThrows(ThermometerException.class, callUpdate);
        assertEquals("Something went wrong", e.getMessage());
        BDDMockito.verify(observer, BDDMockito.never()).update(null);
    }
}
