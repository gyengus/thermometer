package hu.gyengus.thermometerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.gyengus.thermometerservice.serial.SerialPortClient;
import hu.gyengus.thermometerservice.thermometer.ArduinoThermometer;
import hu.gyengus.thermometerservice.thermometer.Thermometer;

@Configuration
public class SpringConfig {
    @Bean
    public Thermometer thermometer() {
        return new ArduinoThermometer(serialPortClient());
    }
    
    @Bean
    public SerialPortClient serialPortClient() {
        return null;
    }
}
