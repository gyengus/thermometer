package hu.gyengus.thermometerservice.domain;

public class Temperature {
    private final double value;
    private final String measurement = "°C";
    
    public Temperature(final double value) {
        this.value = value;
    }
    
    public double getTemperature() {
        return value;
    }
    
    public String getMeasurement() {
        return measurement;
    }
}
