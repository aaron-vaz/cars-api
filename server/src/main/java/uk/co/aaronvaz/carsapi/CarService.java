package uk.co.aaronvaz.carsapi;

import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.db.Car;

@Service
class CarService {
    private final CarRepository repository;

    CarService(final CarRepository repository) {
        this.repository = repository;
    }

    /**
     * Process a {@link CreateOrUpdateCarRequestV1} request to add a new {@link Car} entity to the
     * DB
     *
     * @param request the request containing the data to add
     * @return A DTO object representing the newly created {@link Car} entity
     */
    CarDto addCar(final CreateOrUpdateCarRequestV1 request) {
        final Car car =
                new Car(
                        UUID.randomUUID(),
                        request.getMake(),
                        request.getModel(),
                        request.getColour(),
                        request.getYear());

        final Car storedCar = repository.save(car);
        return convertToDto(storedCar);
    }

    private static CarDto convertToDto(final Car car) {
        return new CarDto(
                car.getId(), car.getMake(), car.getModel(), car.getColour(), car.getYear());
    }
}
