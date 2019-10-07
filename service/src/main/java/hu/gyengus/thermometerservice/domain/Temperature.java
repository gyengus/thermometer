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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Temperature other = (Temperature) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }
}
