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

    private final String portName;
    private final SerialPort serialPort;

    public SerialPortClient(final SerialPort serialPort, final int baudRate) {
        this.serialPort = serialPort;
        portName = serialPort.getSystemPortName();
        this.serialPort.setComPortParameters(baudRate, 8, 1, 0);
        this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        LOG.info("Serial port data: {} {}", portName, baudRate);
    }

    public boolean open() {
        boolean result = true;
        if (!serialPort.isOpen() && !serialPort.openPort()) {
            LOG.error("Unable to open serial port: {}", portName);
            result = false;
        }
        return result;
    }

    public boolean close() {
        boolean result = true;
        if (serialPort.isOpen() && !serialPort.closePort()) {
            LOG.error("Unable to close serial port.");
            result = false;
        }
        return result;
    }

    public boolean isOpen() {
        return serialPort.isOpen();
    }

    public String read() {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            line = reader.readLine();
        } catch (Exception e) {
            throw new SerialPortException("Error when reading serial port: " + e.getMessage());
        }
        return line;
    }

    public void write(final String text) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()))) {
            writer.write(text);
        } catch (Exception e) {
            throw new SerialPortException("Error when writing to serial port: " + e.getMessage());
        }
    }
    
    public void destroy() {
        LOG.info("Closing serial port if required");
        close();
    }
}
