package uk.co.aaronvaz.carsapi;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.aaronvaz.carsapi.datamuse.DatamuseRestApi;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.api.ModelDto;
import uk.co.aaronvaz.carsapi.model.api.UpdateCarRequestV1;
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
     * Process a {@link CreateCarRequestV1} request to add a new {@link Car} entity to the DB
     *
     * @param request the request containing the data to add
     * @return A DTO object representing the newly created {@link Car} entity
     */
    CarDto addCar(final CreateCarRequestV1 request) {
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
     * Process a {@link UpdateCarRequestV1} request to update an existing car in the DB
     *
     * <p>The update is only carried out if the request contains new data Otherwise, no update is
     * performed
     *
     * @param id the id of the existing car
     * @param request the request that will be used to update the car
     * @throws CarNotFoundException if the supplied id is for a non-existent Car
     */
    void updateCar(final UUID id, final UpdateCarRequestV1 request) throws CarNotFoundException {
        final Optional<Car> storedCar = repository.findById(id);
        if (storedCar.isEmpty()) {
            throw new CarNotFoundException(id);
        }

        storedCar.flatMap(car -> updateCarFromRequest(request, car)).ifPresent(repository::save);
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

    /**
     * Find {@link Car} entities in the DB who match the supplied make
     *
     * @param make the make to search for
     * @return Collection of {@link CarDto} from matching {@link Car} entities
     */
    Collection<CarDto> findCarsByMake(final String make) {
        return repository.findByMake(make).stream()
                .map(this::convertToDto)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Find {@link Car} entities in the DB who match the supplied make & model
     *
     * @param make the make to search for
     * @param model the model to search for
     * @return Collection of {@link CarDto} from matching {@link Car} entities
     */
    Collection<CarDto> findCarsByMakeAndModel(final String make, final String model) {
        return repository.findByMakeAAndModel(make, model).stream()
                .map(this::convertToDto)
                .collect(Collectors.toUnmodifiableList());
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

    private Optional<Car> updateCarFromRequest(final UpdateCarRequestV1 request, final Car car) {
        final String make = Objects.requireNonNullElse(request.getMake(), car.getMake());
        final String model = Objects.requireNonNullElse(request.getModel(), car.getModel());
        final String colour = Objects.requireNonNullElse(request.getColour(), car.getColour());
        final Integer year = Objects.requireNonNullElse(request.getYear(), car.getYear());

        final Car updatedCar = new Car(car.getId(), make, model, colour, year);
        return Objects.equals(car, updatedCar) ? Optional.empty() : Optional.of(updatedCar);
    }
}
