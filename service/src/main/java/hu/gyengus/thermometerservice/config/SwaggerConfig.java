package hu.gyengus.thermometerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig {
    @Autowired
    private Environment env;
    @Bean
    public OpenAPI thermometerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(env.getProperty("info.app.name"))
                        .description("")
                        .contact(new Contact()
                                .name("Gyengus")
                                .url("https://github.com/gyengus/thermometer")
                        )
                );
    }
}
