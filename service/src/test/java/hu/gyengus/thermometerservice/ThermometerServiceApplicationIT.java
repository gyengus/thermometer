package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import hu.gyengus.thermometerservice.serial.SerialPortClient;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
class ThermometerServiceApplicationIT {
    @MockBean
    private SerialPortClient serialPortClient;

    @Autowired
    private MockMvc mvc;
    
    @Test
    void testHomeEndpointShouldReturnTemperatureWhenSerialConnectionIsOK() throws Exception {
        // GIVEN
        Mockito.when(serialPortClient.isOpen()).thenReturn(true);
        Mockito.when(serialPortClient.read()).thenReturn("3.14");
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        JSONAssert.assertEquals("{\"measurement\":\"°C\",\"temperature\":3.14}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void testHomeEndpointShouldReturnErrorWhenSerialConnectionIsFail() throws Exception {
        // GIVEN
        Mockito.when(serialPortClient.isOpen()).thenReturn(false);
        Mockito.when(serialPortClient.open()).thenReturn(false);
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        JSONAssert.assertEquals("{\"status\":500,\"message\":\"Error when requesting temperature: Unable to open serial port.\"}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    void testHomeEndpointShouldReturnErrorWhenSerialConnectionIsOKButGotError() throws Exception {
        // GIVEN
        Mockito.when(serialPortClient.isOpen()).thenReturn(true);
        Mockito.when(serialPortClient.read()).thenReturn("ERROR: Something went wrong");
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        JSONAssert.assertEquals("{\"status\":500,\"message\":\"Error when requesting temperature: Something went wrong\"}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    void testHomeEndpointShouldReturnErrorWhenTemperatureIsNaN() throws Exception {
        // GIVEN
        Mockito.when(serialPortClient.isOpen()).thenReturn(true);
        Mockito.when(serialPortClient.read()).thenReturn("Not a number");
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        JSONAssert.assertEquals("{\"status\":500,\"message\":\"Error when requesting temperature: For input string: \\\"Not a number\\\"\"}", result.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        assertEquals(500, result.getResponse().getStatus());
    }
}
