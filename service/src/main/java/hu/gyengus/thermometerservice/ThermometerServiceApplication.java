package hu.gyengus.thermometerservice;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import io.micrometer.core.instrument.Counter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@SpringBootApplication
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
@Api
public class ThermometerServiceApplication {
    private final Thermometer thermometer;
    private Counter thermometerRequests;

    public ThermometerServiceApplication(final Thermometer thermometer, final Counter thermometerRequests) {
        this.thermometer = thermometer;
        this.thermometerRequests = thermometerRequests;
    }

    @GetMapping("/")
    @ApiOperation(value = "Returns the current temperature")
    public Temperature home() {
        thermometerRequests.increment();
        return thermometer.getTemperature();
    }

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(ThermometerServiceApplication.class);
        app.run(args);
    }

}
