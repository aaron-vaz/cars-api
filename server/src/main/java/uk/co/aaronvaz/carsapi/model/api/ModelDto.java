package uk.co.aaronvaz.carsapi.model.api;

import java.util.Objects;

public class ModelDto {
    private final String name;

    private final String homophones;

    public ModelDto(final String name, final String homophones) {
        this.name = name;
        this.homophones = homophones;
    }

    public String getName() {
        return name;
    }

    public String getHomophones() {
        return homophones;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ModelDto modelDto = (ModelDto) o;
        return Objects.equals(name, modelDto.name)
                && Objects.equals(homophones, modelDto.homophones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, homophones);
    }
}
