package hu.gyengus.thermometerservice.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.gyengus.thermometerservice.logging.LoggedException;
import jssc.SerialPort;

public class SerialPortClient {
    private static final Logger LOG = LoggerFactory.getLogger(SerialPortClient.class);
    private final int BAUDRATE;

    private final String portName;
    private final SerialPort serialPort;

    public SerialPortClient(final SerialPort serialPort, final int baudRate, final int timeout) {
        this.serialPort = serialPort;
        this.BAUDRATE = baudRate;
        portName = serialPort.getPortName();
        LOG.info("Serial port name: {}, baudrate: {} bps, timeout: {} ms", portName, baudRate, timeout);
    }

    @LoggedException
    public boolean open() {
        boolean result = true;
        try {
            if (!serialPort.isOpened()) {
                if (serialPort.openPort()) {
                    this.serialPort.setParams(BAUDRATE, 8, 1, SerialPort.PARITY_NONE);
                } else {
                    LOG.error("Unable to open serial port: {}", portName);
                    result = false;
                }
            }
        } catch (jssc.SerialPortException e) {
            throw new SerialPortException("Error when opening serial port: " + e.getMessage());
        }
        return result;
    }

    @LoggedException
    public boolean close() {
        boolean result = true;
        try {
            if (serialPort.isOpened() && !serialPort.closePort()) {
                LOG.error("Unable to close serial port.");
                result = false;
            }
        } catch (jssc.SerialPortException e) {
            throw new SerialPortException("Error when closing serial port: " + e.getMessage());
        }
        return result;
    }

    public boolean isOpen() {
        return serialPort.isOpened();
    }

    @LoggedException
    public String read() {
        String line = null;
        try {
            waitForData();
            line = serialPort.readString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Received from serial port: {}", line);
            }
        } catch (jssc.SerialPortException e) {
            throw new SerialPortException("Error when reading serial port: " + e.getMessage());
        }
        return line;
    }

    private void waitForData() throws jssc.SerialPortException {
        while (serialPort.getInputBufferBytesCount() < 1);
    }

    @LoggedException
    public void write(final String text) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Write to serial port: {}", text);
            }
            if (!serialPort.writeString(text)) {
                throw new SerialPortException("Error when writing to serial port.");
            }
        } catch (jssc.SerialPortException e) {
            throw new SerialPortException("Error when writing to serial port: " + e.getMessage());
        }
    }

    public void destroy() {
        LOG.info("Closing serial port if required");
        close();
    }
}
