package hu.gyengus.thermometerservice.thermometer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.serial.SerialPortHandler;

public class ArduinoThermometer implements Thermometer {
    private static final Logger LOG = LoggerFactory.getLogger(ArduinoThermometer.class);
    private SerialPortHandler serialPort;

    public ArduinoThermometer(SerialPortHandler serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public Temperature getTemperature() {
        Temperature temperature = new Temperature(85.2);
        try {
            openConnectionIfNeeded();
            sendReadCommand();
            temperature = new Temperature(parseAnswer(readAnswer()));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return temperature;
    }

    private void sendReadCommand() {
        sendCommand("READTEMP");
    }
    
    private void sendCommand(final String command) {
        serialPort.write(command + "\n");
    }
    
    private String readAnswer() {
        return serialPort.read();
    }
    
    private double parseAnswer(final String answer) {
        if (answer.startsWith("ERROR: ")) {
            throw new RuntimeException(answer.substring(7));
        }
        return Double.parseDouble(answer.trim());
    }
    
    private void openConnectionIfNeeded() {
        if (!serialPort.isOpen()) {
            if (!serialPort.open()) {
                throw new RuntimeException("Unable to open serial port.");
            }
        }        
    }
}
