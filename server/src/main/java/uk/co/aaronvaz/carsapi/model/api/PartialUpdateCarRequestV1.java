package uk.co.aaronvaz.carsapi.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.springframework.lang.Nullable;

public class PartialUpdateCarRequestV1 {

    private final String make;

    private final String model;

    private final String colour;

    private final Integer year;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PartialUpdateCarRequestV1(
            @JsonProperty("make") final String make,
            @JsonProperty("model") final String model,
            @JsonProperty("colour") final String colour,
            @JsonProperty("year") final Integer year) {
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.year = year;
    }

    @Nullable
    public String getMake() {
        return make;
    }

    @Nullable
    public String getModel() {
        return model;
    }

    @Nullable
    public String getColour() {
        return colour;
    }

    @Nullable
    public Integer getYear() {
        return year;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PartialUpdateCarRequestV1 that = (PartialUpdateCarRequestV1) o;
        return Objects.equals(make, that.make)
                && Objects.equals(model, that.model)
                && Objects.equals(colour, that.colour)
                && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(make, model, colour, year);
    }
}
