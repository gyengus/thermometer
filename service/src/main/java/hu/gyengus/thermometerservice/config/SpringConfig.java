package hu.gyengus.thermometerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import hu.gyengus.thermometerservice.data.DBClient;
import hu.gyengus.thermometerservice.data.InfluxDBClient;
import hu.gyengus.thermometerservice.serial.SerialPortClient;
import hu.gyengus.thermometerservice.thermometer.ArduinoThermometer;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jssc.SerialPort;

@Configuration
@EnableScheduling
public class SpringConfig {
    @Bean
    public Thermometer thermometer(final Environment env) {
        return new ArduinoThermometer(serialPortClient(env));
    }

    @Bean(destroyMethod = "destroy")
    public SerialPortClient serialPortClient(final Environment env) {
        final SerialPort serialPort = new SerialPort(env.getProperty("serial.portName"));
        return new SerialPortClient(serialPort, Integer.valueOf(env.getProperty("serial.baudRate")));
    }

    @Bean
    public TimedAspect timedAspect(final MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

    @Bean
    public Counter thermometerRequests(final MeterRegistry meterRegistry) {
        return Counter.builder("thermometer.requests").register(meterRegistry);
    }

    @Bean
    public DBClient dBClient(final Environment env) {
        DBClient dBClient = null;
        if (!env.getProperty("management.metrics.export.influx.uri").isEmpty() && !env.getProperty("management.metrics.export.influx.user-name").isEmpty()) {
            dBClient = new InfluxDBClient(env.getProperty("management.metrics.export.influx.uri"), env.getProperty("management.metrics.export.influx.user-name"), env.getProperty("management.metrics.export.influx.password"));
        }
        return dBClient;
    }
}
