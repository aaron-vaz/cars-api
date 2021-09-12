package uk.co.aaronvaz.carsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CarsApiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CarsApiApplication.class, args);
    }
}
