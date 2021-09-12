package uk.co.aaronvaz.carsapi.model.db;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Car {
    @Id private UUID id;

    private String make;

    private String model;

    private String colour;

    private int year;

    public Car() {}

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

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(final String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(final String colour) {
        this.colour = colour;
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
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
