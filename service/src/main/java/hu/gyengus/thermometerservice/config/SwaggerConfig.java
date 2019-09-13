package hu.gyengus.thermometerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Autowired
    private Environment env;
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                   .select()
                   .apis(RequestHandlerSelectors.any())
                   .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                   .paths(PathSelectors.any())
                   .build()
                   .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(env.getProperty("info.app.name"))
                .contact(new Contact("Gyengus", "https://github.com/gyengus/thermometer", ""))
                .build();
    }
}
