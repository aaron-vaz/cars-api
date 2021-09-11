package uk.co.aaronvaz.carsapi.model.api;

import java.util.Objects;
import java.util.UUID;

public class CarDto {
    private final UUID id;

    private final String make;

    private final String model;

    private final String colour;

    private final int year;

    public CarDto(
            final UUID id,
            final String make,
            final String model,
            final String colour,
            final int year) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.year = year;
    }

    public UUID getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CarDto carDto = (CarDto) o;
        return year == carDto.year
                && Objects.equals(id, carDto.id)
                && Objects.equals(make, carDto.make)
                && Objects.equals(model, carDto.model)
                && Objects.equals(colour, carDto.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, make, model, colour, year);
    }
}
