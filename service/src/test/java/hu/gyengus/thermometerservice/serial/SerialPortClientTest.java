package hu.gyengus.thermometerservice.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fazecast.jSerialComm.SerialPort;

class SerialPortClientTest {
    @Mock
    private SerialPort serialPort;

    private SerialPortClient underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new SerialPortClient(serialPort, 9600);
    }

    @Test
    void testOpenShouldTrueWhenPortOpenSuccess() {
        // GIVEN
        BDDMockito.given(serialPort.openPort()).willReturn(true);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).openPort();
    }

    @Test
    void testOpenShouldTrueWhenPortIsOpened() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.never()).openPort();
    }

    @Test
    void testOpenShouldFalseWhenPortOpenFailed() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(false);
        BDDMockito.given(serialPort.openPort()).willReturn(false);
        // WHEN
        boolean actual = underTest.open();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).openPort();
    }

    @Test
    void testCloseShouldTrueWhenPortCloseSuccess() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        BDDMockito.given(serialPort.closePort()).willReturn(true);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).closePort();
    }

    @Test
    void testCloseShouldTrueWhenPortIsClosed() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(false);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.never()).closePort();
    }

    @Test
    void testCloseShouldFalseWhenPortCloseFailed() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        BDDMockito.given(serialPort.closePort()).willReturn(false);
        // WHEN
        boolean actual = underTest.close();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
        BDDMockito.verify(serialPort, BDDMockito.times(1)).closePort();
    }

    @Test
    void testIsOpenShouldTrueIfPortOpened() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(true);
        // WHEN
        boolean actual = underTest.isOpen();
        // THEN
        assertEquals(true, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
    }

    @Test
    void testIsOpenShouldFalseIfPortClosed() {
        // GIVEN
        BDDMockito.given(serialPort.isOpen()).willReturn(false);
        // WHEN
        boolean actual = underTest.isOpen();
        // THEN
        assertEquals(false, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).isOpen();
    }

    @Test
    void testRead() {
        // GIVEN
        final String expectedString = "test data";
        InputStream inputStream = new ByteArrayInputStream(expectedString.getBytes());
        BDDMockito.given(serialPort.getInputStream()).willReturn(inputStream);
        // WHEN
        String actual = underTest.read();
        // THEN
        assertEquals(expectedString, actual);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).getInputStream();
    }

    @Test
    void testReadShouldThrowExceptionOnError() {
        // GIVEN
        SerialPortException expectedException = new SerialPortException("Error");
        BDDMockito.given(serialPort.getInputStream()).willThrow(expectedException);
        // WHEN
        Executable callRead = () -> underTest.read();
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callRead);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).getInputStream();
        assertEquals("Error when reading serial port: Error", e.getMessage());
    }

    @Test
    void testWrite() {
        // GIVEN
        final String testString = "test data";
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BDDMockito.given(serialPort.getOutputStream()).willReturn(outputStream);
        // WHEN
        underTest.write(testString);
        // THEN
        assertEquals(testString, outputStream.toString());
        BDDMockito.verify(serialPort, BDDMockito.times(1)).getOutputStream();
    }

    @Test
    void testWriteShouldThrowExceptionWhenFailed() {
        // GIVEN
        SerialPortException expectedException = new SerialPortException("Error");
        BDDMockito.given(serialPort.getOutputStream()).willThrow(expectedException);
        // WHEN
        Executable callWrite = () -> underTest.write("test");
        // THEN
        SerialPortException e = assertThrows(SerialPortException.class, callWrite);
        BDDMockito.verify(serialPort, BDDMockito.times(1)).getOutputStream();
        assertEquals("Error when writing to serial port: Error", e.getMessage());
    }
}
