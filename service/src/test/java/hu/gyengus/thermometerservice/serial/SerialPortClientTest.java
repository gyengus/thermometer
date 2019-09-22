package hu.gyengus.thermometerservice.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jssc.SerialPort;

class SerialPortClientTest {
    @Mock
    private SerialPort serialPort;

    private SerialPortClient underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new SerialPortClient(serialPort, 9600, 2000);
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
    void testRead() throws jssc.SerialPortException {
        // GIVEN
        final String expectedString = "test data";
        BDDMockito.given(serialPort.readString()).willReturn(expectedString);
        BDDMockito.given(serialPort.getInputBufferBytesCount()).willReturn(5);
        // WHEN
        String actual = underTest.read();
        // THEN
        assertEquals(expectedString, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).readString();
    }

    @Test
    void testReadShouldThrowExceptionOnError() throws jssc.SerialPortException {
        // GIVEN
        String expectedErrorMessage = "Error when reading serial port: Port name - ; Method name - ; Exception type - Error.";
        jssc.SerialPortException expectedException = new jssc.SerialPortException("", "", "Error");
        BDDMockito.given(serialPort.readString()).willThrow(expectedException);
        BDDMockito.given(serialPort.getInputBufferBytesCount()).willReturn(5);
        // WHEN
        Executable callRead = () -> underTest.read();
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callRead);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).readString();
        assertEquals(expectedErrorMessage, e.getMessage());
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
    void testWriteShouldThrowExceptionWhenSerialPortThrowsExcerption() throws jssc.SerialPortException {
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
}
