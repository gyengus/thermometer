package hu.gyengus.thermometerservice;

import hu.gyengus.thermometerservice.domain.Temperature;

public interface TemperatureObserver {
    public void update(final Temperature temperature);
}
