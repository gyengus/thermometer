package hu.gyengus.thermometerservice.serial;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortHandler implements hu.gyengus.thermometerservice.serial.SerialPort {
    private String portName = "/dev/ttyACM1";
    private com.fazecast.jSerialComm.SerialPort serialPort;

    public SerialPortHandler(final SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void setPort(String port) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean open() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean close() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String read() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void write(String text) {
        // TODO Auto-generated method stub

    }

}
