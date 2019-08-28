package hu.gyengus.thermometerservice.serial;

public interface SerialPortHandler {
    void setPort(final String port);
    boolean open();
    boolean close();
    boolean isOpen();
    String read();
    void write(final String text);
}