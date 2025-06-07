package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import hu.gyengus.thermometerservice.serial.SerialPortClient;
import hu.gyengus.thermometerservice.thermometer.Thermometer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
public class SwaggerIT {
    private static final int HTTP_STATUS_OK = 200;
    private static final String SWAGGER_JSON_URL = "/v3/api-docs";
    private static final String SWAGGER_UI_URL = "/swagger-ui/index.html";

    @Autowired
    private MockMvc mvc;

    @MockitoBean(extraInterfaces = { TemperatureSubject.class, Observer.class })
    private Thermometer thermometer;

    @MockitoBean
    private SerialPortClient serialPortClient;

    @Test
    void testSwaggerJsonEndpointShouldReturnOK() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(SWAGGER_JSON_URL)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }

    @Test
    void testSwaggerUIEndpointShouldReturnOK() throws Exception {
        // GIVEN
        // WHEN
        MvcResult result = mvc.perform(get(SWAGGER_UI_URL)).andReturn();
        // THEN
        assertEquals(HTTP_STATUS_OK, result.getResponse().getStatus());
    }
}
