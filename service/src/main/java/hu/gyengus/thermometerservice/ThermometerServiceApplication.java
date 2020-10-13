package hu.gyengus.thermometerservice;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.logging.LoggedException;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import io.micrometer.core.instrument.Counter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

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
@RestController
@Api
public class ThermometerServiceApplication implements TemperatureObserver {
    private static final Logger LOG = LoggerFactory.getLogger(ThermometerServiceApplication.class);
    private final Thermometer thermometer;
    private Temperature temperature;
    private Counter thermometerRequests;

    public ThermometerServiceApplication(final Thermometer thermometer, final Counter thermometerRequests) {
        this.thermometer = thermometer;
        ((TemperatureSubject) this.thermometer).setObserver(this);
        this.thermometerRequests = thermometerRequests;
    }

    @GetMapping("/")
    @ApiOperation(value = "Returns the current temperature", response = Temperature.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(@ExampleProperty(mediaType = "*/*", value = "{\"temperature\": 24.5, \"measurement\": \"°C\"}"))),
            @ApiResponse(code = 500, message = "Error", examples = @Example(@ExampleProperty(mediaType = "*/*", value = "{\"status\": 500, \"message\": \"Something went wrong\"}")))
    })
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
