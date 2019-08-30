package hu.gyengus.thermometerservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import hu.gyengus.thermometerservice.serial.SerialPortHandler;

@SpringBootTest(classes = { ThermometerServiceApplication.class })
@AutoConfigureMockMvc
class ThermometerServiceApplicationIT {
    @MockBean
    private SerialPortHandler serialPortHandler;

    @Autowired
    private MockMvc mvc;
    
    @Test
    void testHomeEndpointShouldReturnTemperatureWhenSerialConnectionIsOK() throws Exception {
        // GIVEN
        Mockito.when(serialPortHandler.isOpen()).thenReturn(true);
        Mockito.when(serialPortHandler.read()).thenReturn("3.14");
        // WHEN
        MvcResult result = mvc.perform(get("/")).andReturn();
        // THEN
        assertEquals("{\"measurement\":\"°C\",\"temperature\":3.14}", result.getResponse().getContentAsString());
    }
}
