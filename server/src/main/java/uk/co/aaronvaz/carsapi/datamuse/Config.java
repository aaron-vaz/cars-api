package uk.co.aaronvaz.carsapi.datamuse;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Bean("datamuse")
    RestTemplate datamuseRestTemplate(
            final Properties properties, final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri(properties.getV1BaseURL()).build();
    }
}
