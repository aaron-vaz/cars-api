package uk.co.aaronvaz.carsapi.model.db;

import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.Id;

public class Car {
    @Id private final UUID id;

    private final String make;

    private final String model;

    private final String colour;

    private final int year;

    public Car(
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Car car = (Car) o;
        return id == car.id
                && year == car.year
                && Objects.equals(make, car.make)
                && Objects.equals(model, car.model)
                && Objects.equals(colour, car.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, make, model, colour, year);
    }
}
