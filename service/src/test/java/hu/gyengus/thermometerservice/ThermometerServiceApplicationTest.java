package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hu.gyengus.thermometerservice.data.DBClient;
import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import io.micrometer.core.instrument.Counter;

class ThermometerServiceApplicationTest {
    @Mock(extraInterfaces = { TemperatureSubject.class, Observer.class })
    private Thermometer thermometer;

    @Mock
    private Counter counter;

    @Mock
    private DBClient dBClient;

    private ThermometerServiceApplication underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new ThermometerServiceApplication(thermometer, counter, dBClient);
    }

    @Test
    void testHome() {
        // GIVEN
        Temperature expected = new Temperature(5.5);
        underTest.update(expected);
        BDDMockito.doNothing().when(counter).increment();
        // WHEN
        Temperature actual = underTest.home();
        // THEN
        BDDMockito.verify(counter, BDDMockito.times(1)).increment();
        assertEquals(expected, actual);
    }

    @Test
    void testStartTemperatureMeasurement() {
        // GIVEN
        BDDMockito.doNothing().when(thermometer).sendReadCommand();
        // WHEN
        underTest.startTemperatureMeasurement();
        // THEN
        BDDMockito.verify(thermometer, BDDMockito.times(1)).sendReadCommand();
    }
}
