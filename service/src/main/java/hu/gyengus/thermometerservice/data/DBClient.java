package hu.gyengus.thermometerservice.data;

import hu.gyengus.thermometerservice.domain.Temperature;

public interface DBClient {
    public void sendTemperature(final Temperature temperature);
    public void destroy();
}
