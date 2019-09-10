package hu.gyengus.thermometerservice.thermometer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hu.gyengus.thermometerservice.StaticAppender;
import hu.gyengus.thermometerservice.domain.Temperature;
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
    void testGetTemperatureWhenSerialPortIsClosedAndOpenIsOK() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(false);
        BDDMockito.given(serialPortClient.open()).willReturn(true);
        double temperature = 11.1;
        BDDMockito.given(serialPortClient.read()).willReturn(Double.toString(temperature));
        // WHEN
        Temperature actual = underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).open();
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).write(READTEMP_COMMAND);
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).read();
        assertEquals(temperature, actual.getTemperature());
    }

    @Test
    void testGetTemperatureWhenSerialPortIsOpen() {
        // GIVEN
        BDDMockito.given(serialPortClient.isOpen()).willReturn(true);
        double temperature = 11.1;
        BDDMockito.given(serialPortClient.read()).willReturn(Double.toString(temperature));
        // WHEN
        Temperature actual = underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPortClient, BDDMockito.times(0)).open();
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).write(READTEMP_COMMAND);
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).read();
        assertEquals(temperature, actual.getTemperature());
    }

    @Test
    void testGetTemperatureShouldThrowExceptionWhenGotAnError() {
        // GIVEN
        final String expectedErrorMessage = "Error when requesting temperature: Something went wrong";
        BDDMockito.given(serialPortClient.isOpen()).willReturn(true);
        BDDMockito.given(serialPortClient.read()).willReturn("ERROR: Something went wrong");
        // WHEN
        Executable callGetTemperature = () -> underTest.getTemperature();
        // THEN
        ThermometerException e = assertThrows(ThermometerException.class, callGetTemperature);
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPortClient, BDDMockito.times(0)).open();
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).write(READTEMP_COMMAND);
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).read();
        assertEquals(expectedErrorMessage, e.getMessage());
    }

    @Test
    void testGetTemperatureShouldThrowExceptionWhenSerialPortIsClosedAndCanNotOpen() {
        // GIVEN
        final String expectedErrorMessage = "Error when requesting temperature: Unable to open serial port.";
        StaticAppender.clearEvents();
        BDDMockito.given(serialPortClient.isOpen()).willReturn(false);
        BDDMockito.given(serialPortClient.open()).willReturn(false);
        // WHEN
        Executable callGetTemperature = () -> underTest.getTemperature();
        // THEN
        ThermometerException e = assertThrows(ThermometerException.class, callGetTemperature);
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPortClient, BDDMockito.times(1)).open();
        BDDMockito.verify(serialPortClient, BDDMockito.times(0)).write(READTEMP_COMMAND);
        BDDMockito.verify(serialPortClient, BDDMockito.times(0)).read();
        assertEquals(expectedErrorMessage, e.getMessage());
    }
}
