package hu.gyengus.thermometerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fazecast.jSerialComm.SerialPort;

import hu.gyengus.thermometerservice.serial.SerialPortClient;
import hu.gyengus.thermometerservice.thermometer.ArduinoThermometer;
import hu.gyengus.thermometerservice.thermometer.Thermometer;

@Configuration
public class SpringConfig {
    @Autowired
    private Environment env;

    @Bean
    public Thermometer thermometer() {
        return new ArduinoThermometer(serialPortClient());
    }

    @Bean(destroyMethod = "destroy")
    public SerialPortClient serialPortClient() {
        SerialPort serialPort = SerialPort.getCommPort(env.getProperty("serial.portName"));
        return new SerialPortClient(serialPort, Integer.valueOf(env.getProperty("serial.baudRate")));
    }
}
