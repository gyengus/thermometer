package hu.gyengus.thermometerservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hu.gyengus.thermometerservice.config.SpringConfig;
import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.thermometer.Thermometer;

@SpringBootApplication
@Import({ SpringConfig.class })
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class,
                                     TaskExecutionAutoConfiguration.class,
                                     TaskSchedulingAutoConfiguration.class,
                                     ValidationAutoConfiguration.class,
                                     WebSocketServletAutoConfiguration.class,
                                     CodecsAutoConfiguration.class,
                                     HttpMessageConvertersAutoConfiguration.class,
                                     ErrorMvcAutoConfiguration.class,
                                     RestTemplateAutoConfiguration.class
                                   })
@RestController
public class ThermometerServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ThermometerServiceApplication.class);

    private final Thermometer thermometer;

    public ThermometerServiceApplication(final Thermometer thermometer) {
        this.thermometer = thermometer;
    }

    @GetMapping("/")
    public Temperature home() {
        try {
            return thermometer.getTemperature();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ThermometerServiceApplication.class);
        app.run(args);
    }

}