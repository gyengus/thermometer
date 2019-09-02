package hu.gyengus.thermometerservice.serial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortClient {
    private static final Logger LOG = LoggerFactory.getLogger(SerialPortClient.class);

    private String portName = "/dev/ttyACM1";
    private SerialPort serialPort;

    public SerialPortClient(final SerialPort serialPort, final int baudRate) {
        this.serialPort = serialPort;
        portName = serialPort.getSystemPortName();
        this.serialPort.setComPortParameters(baudRate, 8, 1, 0);
        this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        LOG.info("Serial port data: " + portName + " " + baudRate);
    }

    public boolean open() {
        if (serialPort.openPort()) {
            return true;
        }
        LOG.error("Unable to open serial port: " + portName);
        return false;
    }

    public boolean close() {
        if (serialPort.closePort()) {
            return true;
        }
        LOG.error("Unable to close serial port.");
        return false;
    }

    public boolean isOpen() {
        return serialPort.isOpen();
    }

    public String read() {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            line = reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Error when reading serial port: " + e.getMessage());
        }
        return line;
    }

    public void write(String text) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()))) {
            writer.write(text);
        } catch (Exception e) {
            throw new RuntimeException("Error when writing to serial port: " + e.getMessage());
        }

    }
}
