package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import hu.gyengus.thermometerservice.thermometer.Thermometer;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
public class SwaggerIT {
    private static final int HTTP_STATUS_OK = 200;
    private static final String SWAGGER_JSON_URL = "/v2/api-docs";
    private static final String SWAGGER_UI_URL = "/swagger-ui.html";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Thermometer thermometer;

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
