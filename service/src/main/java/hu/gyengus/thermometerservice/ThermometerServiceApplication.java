package hu.gyengus.thermometerservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.logging.LoggedException;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import io.micrometer.core.instrument.Counter;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class,
                                     TaskExecutionAutoConfiguration.class,
                                     ValidationAutoConfiguration.class,
                                     WebSocketServletAutoConfiguration.class,
                                     CodecsAutoConfiguration.class,
                                     HttpMessageConvertersAutoConfiguration.class,
                                     ErrorMvcAutoConfiguration.class,
                                     RestTemplateAutoConfiguration.class
                                   })
@EnableAspectJAutoProxy
@RestController
@Tag(name = "ThermometerService", description = "Thermometer service API")
public class ThermometerServiceApplication implements TemperatureObserver {
    private static final Logger LOG = LoggerFactory.getLogger(ThermometerServiceApplication.class);
    private final Thermometer thermometer;
    private Temperature temperature;
    private final Counter thermometerRequests;

    public ThermometerServiceApplication(final Thermometer thermometer, final Counter thermometerRequests) {
        this.thermometer = thermometer;
        ((TemperatureSubject) this.thermometer).setObserver(this);
        this.thermometerRequests = thermometerRequests;
    }

    @GetMapping("/")
    @Operation(summary = "Returns the current temperature")
    public Temperature home() {
        thermometerRequests.increment();
        return temperature;
    }

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(ThermometerServiceApplication.class);
        app.run(args);
    }

    @Scheduled(fixedDelayString = "${serial.readIntervall}")
    @LoggedException
    public void startTemperatureMeasurement() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduled sendReadCommand call");
        }
        thermometer.sendReadCommand();
    }

    public void update(final Temperature temperature) {
        this.temperature = temperature;
    }
}
