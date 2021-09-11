package uk.co.aaronvaz.carsapi;

import java.util.Objects;
import java.util.UUID;

public class CarNotFoundException extends Exception {
    private final UUID id;

    public CarNotFoundException(final UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "No Car found for id: " + id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CarNotFoundException that = (CarNotFoundException) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
