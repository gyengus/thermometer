package hu.gyengus.thermometerservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "The temperature value and measurement")
public class Temperature {
    @ApiModelProperty(example = "°C", value = "The measurement of the value")
    private static final String MEASUREMENT = "°C";

    @ApiModelProperty(example = "24.5", value = "The value")
    private final double value;

    public Temperature(final double value) {
        this.value = value;
    }

    public double getTemperature() {
        return value;
    }

    public String getMeasurement() {
        return MEASUREMENT;
    }
}
