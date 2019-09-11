package hu.gyengus.thermometerservice.thermometer;

import hu.gyengus.thermometerservice.domain.Command;
import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.logging.LoggedException;
import hu.gyengus.thermometerservice.serial.SerialPortClient;

public class ArduinoThermometer implements Thermometer {
    private final SerialPortClient serialPortClient;

    public ArduinoThermometer(final SerialPortClient serialPortClient) {
        this.serialPortClient = serialPortClient;
    }

    @Override
    @LoggedException
    public Temperature getTemperature() {
        Temperature temperature;
        try {
            openConnectionIfNeeded();
            sendReadCommand();
            temperature = new Temperature(parseAnswer(readAnswer()));
        } catch (Exception e) {
            final String errorMessage = "Error when requesting temperature: " + e.getMessage();
            throw new ThermometerException(errorMessage);
        }
        return temperature;
    }

    private void sendReadCommand() {
        sendCommand(Command.READTEMP.name());
    }

    private void sendCommand(final String command) {
        serialPortClient.write(command + "\n");
    }

    private String readAnswer() {
        return serialPortClient.read();
    }

    @LoggedException
    private double parseAnswer(final String answer) {
        if (answer.startsWith("ERROR: ")) {
            throw new ThermometerException(answer.substring(7));
        }
        return Double.parseDouble(answer.trim());
    }

    @LoggedException
    private void openConnectionIfNeeded() {
        if (!serialPortClient.isOpen() && !serialPortClient.open()) {
            throw new ThermometerException("Unable to open serial port.");
        }
    }

    @Override
    public boolean isConnected() {
        return serialPortClient.isOpen();
    }
}
