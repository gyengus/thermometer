package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import hu.gyengus.thermometerservice.thermometer.ThermometerException;

class ThermometerServiceApplicationTest {
    @Mock
    private Thermometer thermometer;

    private ThermometerServiceApplication underTest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new ThermometerServiceApplication(thermometer);
    }

    @Test
    void testHome() {
        // GIVEN
        Temperature expected = new Temperature(5.5);
        BDDMockito.given(thermometer.getTemperature()).willReturn(expected);
        // WHEN
        Temperature actual = underTest.home();
        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void testHomeShouldThrowExceptionWhenGotError() {
        // GIVEN
        StaticAppender.clearEvents();
        Exception expectedException = new ThermometerException("Something went wrong");
        BDDMockito.given(thermometer.getTemperature()).willThrow(expectedException);
        // WHEN
        Executable callHome = () -> underTest.home();
        // THEN
        ResponseStatusException e = assertThrows(ResponseStatusException.class, callHome);
        assertEquals("Something went wrong", StaticAppender.getEvents().get(0).getMessage());
        assertEquals("500 INTERNAL_SERVER_ERROR \"Error\"; nested exception is hu.gyengus.thermometerservice.thermometer.ThermometerException: Something went wrong", e.getMessage());
        assertEquals("Error", e.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
    }

}
