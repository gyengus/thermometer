package hu.gyengus.thermometerservice.thermometer;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import hu.gyengus.thermometerservice.Observer;
import hu.gyengus.thermometerservice.TemperatureObserver;
import hu.gyengus.thermometerservice.TemperatureSubject;
import hu.gyengus.thermometerservice.domain.Command;
import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.logging.LoggedException;
import hu.gyengus.thermometerservice.serial.SerialPortClient;

public class ArduinoThermometer implements Thermometer, Observer, TemperatureSubject {
    @Value("${serial.readIntervall:5000}")
    private int waitAfterOpenSerialPort;
    private static final Logger LOG = LoggerFactory.getLogger(ArduinoThermometer.class);
    private final SerialPortClient serialPortClient;
    private Temperature temperature;
    private TemperatureObserver observer;

    public ArduinoThermometer(final SerialPortClient serialPortClient) {
        this.serialPortClient = serialPortClient;
        this.serialPortClient.setObserver(this);
    }

    @LoggedException
    public void sendReadCommand() {
        openConnectionIfNeeded();
        sendCommand(Command.READTEMP.name());
    }

    private void sendCommand(final String command) {
        serialPortClient.write(command + "\n");
    }

    @LoggedException
    private double parseAnswer(final String answer) {
        if (answer.startsWith("ERROR: ")) {
            throw new ThermometerException(answer.substring(7));
        }
        return Double.parseDouble(answer.trim());
    }

    @LoggedException
    public void openConnectionIfNeeded() {
        if (!serialPortClient.isOpen()) {
            if (serialPortClient.open()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(waitAfterOpenSerialPort);
                } catch (InterruptedException e) {
                    LOG.error("Error while sleeping: {}", e.getMessage());
                }
            } else {
                throw new ThermometerException("Unable to open serial port.");
            }
        }
    }

    @Override
    public boolean isConnected() {
        return serialPortClient.isOpen();
    }

    @Override
    public void update(final String message) {
        if (observer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Received data, parse it and notifying observer");
            }
            temperature = new Temperature(parseAnswer(message));
            observer.update(temperature);
        }
    }

    @Override
    public void setObserver(final TemperatureObserver observer) {
        this.observer = observer;
    }
}
