package hu.gyengus.thermometerservice.serial;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortClient {
    private String portName = "/dev/ttyACM1";
    private com.fazecast.jSerialComm.SerialPort serialPort;

    public SerialPortClient(final SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void setPort(String port) {
        // TODO Auto-generated method stub

    }

    public boolean open() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean close() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    public String read() {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(String text) {
        // TODO Auto-generated method stub

    }
}
