package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import hu.gyengus.thermometerservice.domain.Temperature;
import hu.gyengus.thermometerservice.serial.SerialPortClient;
import hu.gyengus.thermometerservice.thermometer.Thermometer;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
class ThermometerServiceApplicationIT {
    @MockitoBean
    private SerialPortClient serialPortClient;

    @MockitoBean(extraInterfaces = { TemperatureSubject.class, Observer.class })
    private Thermometer thermometer;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ThermometerServiceApplication thermometerServiceApplication;

    @Test
    void testHomeEndpointShouldReturnTemperature() throws Exception {
        // GIVEN
        thermometerServiceApplication.update(new Temperature(3.14));
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        JSONAssert.assertEquals("{\"measurement\":\"°C\",\"temperature\":3.14}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void testHomeEndpointShouldReturnEmptyStringWhenTemperatureIsNull() throws Exception {
        // GIVEN
        thermometerServiceApplication.update(null);
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        assertEquals("", result.getResponse().getContentAsString());
    }
}
