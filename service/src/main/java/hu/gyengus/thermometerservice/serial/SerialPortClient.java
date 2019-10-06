package hu.gyengus.thermometerservice.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.gyengus.thermometerservice.Observer;
import hu.gyengus.thermometerservice.Subject;
import hu.gyengus.thermometerservice.logging.LoggedException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

public class SerialPortClient implements SerialPortEventListener, Subject {
    private static final Logger LOG = LoggerFactory.getLogger(SerialPortClient.class);
    private final int baudRate;

    private final String portName;
    private final SerialPort serialPort;
    private Observer observer;

    public SerialPortClient(final SerialPort serialPort, final int baudRate) {
        this.serialPort = serialPort;
        this.baudRate = baudRate;
        portName = serialPort.getPortName();
        LOG.info("Serial port name: {}, baudrate: {} bps", portName, baudRate);
    }

    @LoggedException
    public boolean open() {
        boolean result = true;
        try {
            if (!serialPort.isOpened()) {
                if (serialPort.openPort()) {
                    this.serialPort.setParams(baudRate, 8, 1, SerialPort.PARITY_NONE);
                    this.serialPort.addEventListener(this);
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

    @LoggedException
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) {
            String buf;
            try {
                buf = serialPort.readString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Received from serial port: {}", buf);
                }
                notifyObserver(buf);
            } catch (jssc.SerialPortException e) {
                throw new SerialPortException("Error when handle SerialPortEvent: " + e.getMessage());
            }
        }

    }

    @Override
    public void setObserver(final Observer observer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting up observer");
        }
        this.observer = observer;
    }

    @Override
    public void notifyObserver(final String message) {
        if (observer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Notifying observer with message: {}", message);
            }
            observer.update(message);
        }
    }
}
