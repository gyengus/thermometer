package hu.gyengus.thermometerservice.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import hu.gyengus.thermometerservice.Observer;
import hu.gyengus.thermometerservice.TemperatureSubject;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import jssc.SerialPort;
import jssc.SerialPortEvent;

class SerialPortClientTest {
    @Mock
    private SerialPort serialPort;

    @Mock(extraInterfaces = { TemperatureSubject.class, Observer.class })
    private Thermometer thermometer;

    private SerialPortClient underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new SerialPortClient(serialPort, 9600);
    }

    @Test
    void testOpenShouldTrueWhenPortOpenSuccess() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.openPort()).willReturn(true);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).openPort();
    }

    @Test
    void testOpenShouldTrueWhenPortIsOpened() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(true);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.never()).openPort();
    }

    @Test
    void testOpenShouldFalseWhenPortOpenFailed() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(false);
        BDDMockito.given(serialPort.openPort()).willReturn(false);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).openPort();
    }

    @Test
    void testOpenShouldThrowExceptionWhenPortOpenFailed() throws jssc.SerialPortException {
        // GIVEN
        String expectedErrorMessage = "Error when opening serial port: Port name - ; Method name - ; Exception type - Error.";
        BDDMockito.given(serialPort.isOpened()).willReturn(false);
        BDDMockito.given(serialPort.openPort()).willThrow(new jssc.SerialPortException("", "", "Error"));
        // WHEN
        Executable callOpen = () -> underTest.open();
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callOpen);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).openPort();
        assertEquals(expectedErrorMessage, e.getMessage());
    }

    @Test
    void testCloseShouldTrueWhenPortCloseSuccess() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(true);
        BDDMockito.given(serialPort.closePort()).willReturn(true);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).closePort();
    }

    @Test
    void testCloseShouldTrueWhenPortIsClosed() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(false);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.never()).closePort();
    }

    @Test
    void testCloseShouldFalseWhenPortCloseFailed() throws jssc.SerialPortException {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(true);
        BDDMockito.given(serialPort.closePort()).willReturn(false);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).closePort();
    }

    @Test
    void testCloseShouldThrowExceptionWhenPortCloseThrowsException() throws jssc.SerialPortException {
        // GIVEN
        String expectedErrorMessage = "Error when closing serial port: Port name - ; Method name - ; Exception type - Error.";
        BDDMockito.given(serialPort.isOpened()).willReturn(true);
        BDDMockito.given(serialPort.closePort()).willThrow(new jssc.SerialPortException("", "", "Error"));
        // WHEN
        Executable callClose = () -> underTest.close();
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callClose);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).closePort();
        assertEquals(expectedErrorMessage, e.getMessage());
    }

    @Test
    void testIsOpenShouldTrueIfPortOpened() {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(true);
        // WHEN
        boolean actual = underTest.isOpen();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
    }

    @Test
    void testIsOpenShouldFalseIfPortClosed() {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(false);
        // WHEN
        boolean actual = underTest.isOpen();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
    }

    @Test
    void testWrite() throws jssc.SerialPortException {
        // GIVEN
        final String testString = "test data";
        BDDMockito.given(serialPort.writeString(testString)).willReturn(true);
        // WHEN
        underTest.write(testString);
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).writeString(testString);
    }

    @Test
    void testWriteShouldThrowExceptionWhenFailed() throws jssc.SerialPortException {
        // GIVEN
        String expectedErrorMessage = "Error when writing to serial port.";
        BDDMockito.given(serialPort.writeString("test")).willReturn(false);
        // WHEN
        Executable callWrite = () -> underTest.write("test");
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callWrite);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).writeString("test");
        assertEquals(expectedErrorMessage, e.getMessage());
    }

    @Test
    void testWriteShouldThrowExceptionWhenSerialPortThrowsException() throws jssc.SerialPortException {
        // GIVEN
        String expectedErrorMessage = "Error when writing to serial port: Port name - ; Method name - ; Exception type - Error.";
        BDDMockito.given(serialPort.writeString("test")).willThrow(new jssc.SerialPortException("", "", "Error"));
        // WHEN
        Executable callWrite = () -> underTest.write("test");
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callWrite);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).writeString("test");
        assertEquals(expectedErrorMessage, e.getMessage());
    }

    @Test
    void testSerialEventShouldCallObserverUpdateWhenReceivedData() throws jssc.SerialPortException {
        // GIVEN
        final String dataFromSerial = "Data";
        BDDMockito.given(serialPort.readString()).willReturn(dataFromSerial);
        SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
        BDDMockito.given(serialPortEvent.isRXCHAR()).willReturn(true);
        Observer observer = Mockito.mock(Observer.class);
        BDDMockito.doNothing().when(observer).update(dataFromSerial);
        // WHEN
        underTest.setObserver(observer);
        underTest.serialEvent(serialPortEvent);
        // THEN
        BDDMockito.verify(observer, BDDMockito.times(1)).update(dataFromSerial);
    }

    @Test
    void testSerialEventShouldNotCallObserverUpdateWhenDoNotReceivedData() throws jssc.SerialPortException {
        // GIVEN
        final String dataFromSerial = "Data";
        BDDMockito.given(serialPort.readString()).willReturn(dataFromSerial);
        SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
        BDDMockito.given(serialPortEvent.isRXCHAR()).willReturn(false);
        Observer observer = Mockito.mock(Observer.class);
        BDDMockito.doNothing().when(observer).update(dataFromSerial);
        // WHEN
        underTest.setObserver(observer);
        underTest.serialEvent(serialPortEvent);
        // THEN
        BDDMockito.verify(observer, BDDMockito.never()).update(dataFromSerial);
    }

    @Test
    void testSerialEventShouldNotCallObserverUpdateWhenReceivedDataButNotSetObserver() throws jssc.SerialPortException {
        // GIVEN
        final String dataFromSerial = "Data";
        BDDMockito.given(serialPort.readString()).willReturn(dataFromSerial);
        SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
        BDDMockito.given(serialPortEvent.isRXCHAR()).willReturn(true);
        Observer observer = Mockito.mock(Observer.class);
        BDDMockito.doNothing().when(observer).update(dataFromSerial);
        // WHEN
        underTest.serialEvent(serialPortEvent);
        // THEN
        BDDMockito.verify(observer, BDDMockito.never()).update(dataFromSerial);
    }

    @Test
    void testSerialEventShouldThrowExceptionWhenGotException() throws jssc.SerialPortException {
        // GIVEN
        jssc.SerialPortException expectedException = new jssc.SerialPortException("", "", "Error");
        BDDMockito.given(serialPort.readString()).willThrow(expectedException);
        SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
        BDDMockito.given(serialPortEvent.isRXCHAR()).willReturn(true);
        // WHEN
        Executable callSerialEvent = () -> underTest.serialEvent(serialPortEvent);
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callSerialEvent);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).readString();
        assertEquals("Error when handle SerialPortEvent: Port name - ; Method name - ; Exception type - Error.", e.getMessage());
    }

    @Test
    void testDestroyShouldCallClose() {
        // GIVEN
        BDDMockito.given(serialPort.isOpened()).willReturn(false); // close() call serialPort.isOpened()
        // WHEN
        underTest.destroy();
        // THEN
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpened();
    }
}
