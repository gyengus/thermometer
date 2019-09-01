package hu.gyengus.thermometerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.gyengus.thermometerservice.serial.SerialPort;
import hu.gyengus.thermometerservice.thermometer.ArduinoThermometer;
import hu.gyengus.thermometerservice.thermometer.Thermometer;

@Configuration
public class SpringConfig {
    @Bean
    public Thermometer thermometer() {
        return new ArduinoThermometer(serialPort());
    }
    
    @Bean
    public SerialPort serialPort() {
        return null;
    }
}
