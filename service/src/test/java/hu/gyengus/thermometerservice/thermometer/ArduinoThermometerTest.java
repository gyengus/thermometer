package hu.gyengus.thermometerservice.thermometer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hu.gyengus.thermometerservice.StaticAppender;
import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.serial.SerialPortHandler;

class ArduinoThermometerTest {

    @Mock
    private SerialPortHandler serialPort;

    private Thermometer underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new ArduinoThermometer(serialPort);
    }

    @Test
    void testGetTemperatureWhenSerialPortIsClosedAndOpenIsOK() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(false);
        BDDMockito.given(serialPort.open()).willReturn(true);
        double temperature = 11.1;
        BDDMockito.given(serialPort.read()).willReturn(Double.toString(temperature));
        // WHEN
        Temperature actual = underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).open();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).write("READTEMP\n");
        BDDMockito.verify(serialPort, BDDMockito.times(1)).read();
        assertEquals(temperature, actual.getTemperature());
    }

    @Test
    void testGetTemperatureWhenSerialPortIsOpen() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        double temperature = 11.1;
        BDDMockito.given(serialPort.read()).willReturn(Double.toString(temperature));
        // WHEN
        Temperature actual = underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(0)).open();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).write("READTEMP\n");
        BDDMockito.verify(serialPort, BDDMockito.times(1)).read();
        assertEquals(temperature, actual.getTemperature());
    }

    @Test
    void testGetTemperatureShouldLogErrorWhenGotAnError() {
        // GIVEN
        StaticAppender.clearEvents();
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        BDDMockito.given(serialPort.read()).willReturn("ERROR: Something went wrong");
        // WHEN
        underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(0)).open();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).write("READTEMP\n");
        BDDMockito.verify(serialPort, BDDMockito.times(1)).read();
        assertEquals("Error when requesting temperature: Something went wrong", StaticAppender.getEvents().get(0).getMessage());
    }

    @Test
    void testGetTemperatureShouldLogErrorWhenSerialPortIsClosedAndCanNotOpen() {
        // GIVEN
        StaticAppender.clearEvents();
        BDDMockito.given(serialPort.isOpen()).willReturn(false);
        BDDMockito.given(serialPort.open()).willReturn(false);
        // WHEN
        underTest.getTemperature();
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).open();
        BDDMockito.verify(serialPort, BDDMockito.times(0)).write("READTEMP\n");
        BDDMockito.verify(serialPort, BDDMockito.times(0)).read();
        assertEquals("Error when requesting temperature: Unable to open serial port.", StaticAppender.getEvents().get(0).getMessage());
    }
}
