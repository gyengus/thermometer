package hu.gyengus.thermometerservice.actuator;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

import hu.gyengus.thermometerservice.thermometer.Thermometer;

@Component
public class ThermometerHealthIndicator extends AbstractHealthIndicator {
    private final Thermometer thermometer;

    public ThermometerHealthIndicator(final Thermometer thermometer) {
        this.thermometer = thermometer;
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        if (thermometer.isConnected()) {
            builder.up();
        } else {
            builder.down();
        }
    }

}
