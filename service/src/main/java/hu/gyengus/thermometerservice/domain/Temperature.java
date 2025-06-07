package hu.gyengus.thermometerservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The temperature value and measurement")
public class Temperature {
    @Schema(example = "°C", description = "The measurement of the value")
    private static final String MEASUREMENT = "°C";

    @Schema(example = "24.5", description = "The value")
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
        return (value == other.value);
    }
}
