package hu.gyengus.thermometerservice.thermometer;

public interface Thermometer {

    boolean isConnected();
    void openConnectionIfNeeded();
    void sendReadCommand();
}
