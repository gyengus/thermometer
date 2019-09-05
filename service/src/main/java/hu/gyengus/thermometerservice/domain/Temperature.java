package hu.gyengus.thermometerservice.domain;

public class Temperature {
    private static final String MEASUREMENT = "°C";
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
