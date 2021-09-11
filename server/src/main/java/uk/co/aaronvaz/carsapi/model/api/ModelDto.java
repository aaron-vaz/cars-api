package uk.co.aaronvaz.carsapi.model.api;

import java.util.Objects;

public class ModelDto {
    private final String model;

    private final String homophones;

    public ModelDto(final String model, final String homophones) {
        this.model = model;
        this.homophones = homophones;
    }

    public String getModel() {
        return model;
    }

    public String getHomophones() {
        return homophones;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ModelDto modelDto = (ModelDto) o;
        return Objects.equals(model, modelDto.model)
                && Objects.equals(homophones, modelDto.homophones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, homophones);
    }
}
