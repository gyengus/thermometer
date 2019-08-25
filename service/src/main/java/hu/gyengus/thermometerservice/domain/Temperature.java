package hu.gyengus.thermometerservice.domain;

public class Temperature {
    private double value;
    private String measurement = "°C";
    
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
