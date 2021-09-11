package uk.co.aaronvaz.carsapi;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.aaronvaz.carsapi.datamuse.DatamuseRestApi;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.api.ModelDto;
import uk.co.aaronvaz.carsapi.model.db.Car;

@Service
class CarService {
    private final CarRepository repository;

    private final DatamuseRestApi datamuseRestApi;

    CarService(final CarRepository repository, final DatamuseRestApi datamuseRestApi) {
        this.repository = repository;
        this.datamuseRestApi = datamuseRestApi;
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

    /**
     * Search the DB for a {@link Car} with the supplied id
     *
     * @param id the id of the {@link Car} entity you are looking for
     * @return Optional containing the found entity's DTO or {@link Optional#empty()} if nothing is
     *     found
     */
    Optional<CarDto> retrieveCar(final UUID id) {
        return repository.findById(id).map(this::convertToDto);
    }

    /**
     * Delete the {@link Car} entry in the db that matches the given id
     *
     * @param id the id of the {@link Car} to delete
     */
    void deleteCar(final UUID id) {
        repository.deleteById(id);
    }

    private CarDto convertToDto(final Car car) {
        final String homophones =
                datamuseRestApi.soundsLike(car.getModel()).stream()
                        .map(SoundsLikeResponseV1::getWord)
                        .limit(5)
                        .collect(Collectors.joining(", "));

        final ModelDto modelDto = new ModelDto(car.getModel(), homophones);

        return new CarDto(car.getId(), car.getMake(), modelDto, car.getColour(), car.getYear());
    }
}
