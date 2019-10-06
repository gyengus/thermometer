package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import hu.gyengus.thermometerservice.thermometer.Thermometer;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
public class ActuatorIT {
    private static final int HTTP_STATUS_OK = 200;
    private static final int HTTP_STATUS_UNAVAILABLE = 503;
    private static final int HTTP_STATUS_NOT_FOUND = 404;
    private static final String ACTUATOR_ROOT = "/actuator";
    private static final String ACTUATOR_METRICS = ACTUATOR_ROOT + "/metrics";
    private static final String ACTUATOR_INFO = ACTUATOR_ROOT + "/info";
    private static final String ACTUATOR_HEALTH = ACTUATOR_ROOT + "/health";

    @Autowired
    private MockMvc mvc;

    @MockBean(extraInterfaces = { TemperatureSubject.class, Observer.class })
    private Thermometer thermometer;

    @Test
    void testActuatorEndpointShouldReturnOK() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_ROOT)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }

    @Test
    void testActuatorHealthEndpointShouldReturnOKWhenThermometerWork() throws Exception {
        // GIVEN
        Mockito.when(thermometer.isConnected()).thenReturn(true);
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_HEALTH)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }

    @Test
    void testActuatorHealthEndpointShouldReturn503WhenThermometerDoesNotWork() throws Exception {
        // GIVEN
        Mockito.when(thermometer.isConnected()).thenReturn(false);
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_HEALTH)).andReturn();
        // THEN
        Mockito.verify(thermometer, BDDMockito.times(1)).isConnected();
        assertEquals(HTTP_STATUS_UNAVAILABLE, result.getResponse().getStatus());
    }

    @Test
    void testActuatorInfoEndpointShouldReturnOK() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_INFO)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }

    @Test
    void testActuatorMetricsEndpointShouldReturnOK() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_METRICS)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }

    @Test
    void testActuatorMetricsTimedGetTemperatureEndpointShouldReturn404BeforeCallRootEndpoint() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_METRICS + "/timed.getTemperature")).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_NOT_FOUND, result.getResponse().getStatus());
    }

    @Test
    void testActuatorMetricsThermometerRequestsEndpointShouldReturnOKBeforeCallRootEndpoint() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(ACTUATOR_METRICS + "/thermometer.requests")).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }
}
