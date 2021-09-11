package uk.co.aaronvaz.carsapi.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class CreateCarRequestV1 {

    @NotBlank private final String make;

    @NotBlank private final String model;

    @NotBlank private final String colour;

    @Positive private final int year;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CreateCarRequestV1(
            @JsonProperty("make") final String make,
            @JsonProperty("model") final String model,
            @JsonProperty("colour") final String colour,
            @JsonProperty("year") final int year) {
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColour() {
        return colour;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CreateCarRequestV1 that = (CreateCarRequestV1) o;
        return year == that.year
                && Objects.equals(make, that.make)
                && Objects.equals(model, that.model)
                && Objects.equals(colour, that.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(make, model, colour, year);
    }
}
