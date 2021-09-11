package uk.co.aaronvaz.carsapi.datamuse;

import javax.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("datamuse.api")
class Properties {

    @NotBlank private final String v1BaseURL;

    @ConstructorBinding
    Properties(@DefaultValue("") final String v1BaseURL) {
        this.v1BaseURL = v1BaseURL;
    }

    String getV1BaseURL() {
        return v1BaseURL;
    }
}
