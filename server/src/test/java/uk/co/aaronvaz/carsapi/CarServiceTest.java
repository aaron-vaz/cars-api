package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import uk.co.aaronvaz.carsapi.datamuse.DatamuseRestApi;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.api.ModelDto;
import uk.co.aaronvaz.carsapi.model.api.UpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.db.Car;

class CarServiceTest {

    private final CarRepository mockRepository = mock(CarRepository.class);

    private final DatamuseRestApi mockDatamuseRestApi = mock(DatamuseRestApi.class);

    private final CarService carService = new CarService(mockRepository, mockDatamuseRestApi);

    @Test
    void addCar_HappyPath_CarAddedToDb() {
        // given
        willAnswer(invocation -> invocation.getArgument(0)).given(mockRepository).save(any());

        final CreateCarRequestV1 request = new CreateCarRequestV1("BMW", "i8", "Silver", 2015);

        final Collection<SoundsLikeResponseV1> homophones =
                List.of(
                        new SoundsLikeResponseV1("a", 95, 1),
                        new SoundsLikeResponseV1("uh", 95, 1));
        willReturn(homophones).given(mockDatamuseRestApi).soundsLike(request.getModel());

        // when
        final CarDto carDto = carService.addCar(request);

        // then
        final ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(mockRepository).save(carCaptor.capture());

        // verify DB model against request data
        final Car storedCar = carCaptor.getValue();
        assertNotNull(storedCar.getId());
        assertEquals(request.getMake(), storedCar.getMake());
        assertEquals(request.getModel(), storedCar.getModel());
        assertEquals(request.getColour(), storedCar.getColour());
        assertEquals(request.getYear(), storedCar.getYear());

        // Verify DTO against request data
        assertNotNull(carDto.getId());
        assertEquals(request.getMake(), carDto.getMake());
        assertEquals(new ModelDto(request.getModel(), "a, uh"), carDto.getModel());
        assertEquals(request.getColour(), carDto.getColour());
        assertEquals(request.getYear(), carDto.getYear());
    }

    @Test
    void updateCar_HappyPath_CarUpdated() throws CarNotFoundException {
        // given
        final Car car = new Car(UUID.randomUUID(), "Hyundai", "i10", "Red", 2004);
        willReturn(Optional.of(car)).given(mockRepository).findById(car.getId());

        final UpdateCarRequestV1 updateCarRequest =
                new UpdateCarRequestV1("Hyundai", "i30", "Black", 2010);

        // when
        carService.updateCar(car.getId(), updateCarRequest);

        // then
        final ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(mockRepository).save(carCaptor.capture());

        final Car updatedCar = carCaptor.getValue();
        assertNotEquals(car, updatedCar);

        assertEquals(car.getId(), updatedCar.getId());
        assertEquals(updateCarRequest.getMake(), updatedCar.getMake());
        assertEquals(updateCarRequest.getModel(), updatedCar.getModel());
        assertEquals(updateCarRequest.getColour(), updatedCar.getColour());
        assertEquals(updateCarRequest.getYear(), updatedCar.getYear());
    }

    @Test
    void updateCar_PartialUpdate_CarUpdated() throws CarNotFoundException {
        // given
        final Car car = new Car(UUID.randomUUID(), "Seat", "Ibiza", "Red", 2010);
        willReturn(Optional.of(car)).given(mockRepository).findById(car.getId());

        final UpdateCarRequestV1 updateCarRequest =
                new UpdateCarRequestV1(null, "Leon", null, null);

        // when
        carService.updateCar(car.getId(), updateCarRequest);

        // then
        final ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(mockRepository).save(carCaptor.capture());

        final Car updatedCar = carCaptor.getValue();
        assertNotEquals(car, updatedCar);

        // check that only model was updated and the rest stayed the same
        assertEquals(car.getId(), updatedCar.getId());
        assertEquals(car.getMake(), updatedCar.getMake());
        assertEquals(updateCarRequest.getModel(), updatedCar.getModel());
        assertEquals(car.getColour(), updatedCar.getColour());
        assertEquals(car.getYear(), updatedCar.getYear());
    }

    @Test
    void updateCar_SamePropsSubmitted_CarNotUpdated() throws CarNotFoundException {
        // given
        final Car car = new Car(UUID.randomUUID(), "Seat", "Ibiza", "Red", 2010);
        willReturn(Optional.of(car)).given(mockRepository).findById(car.getId());

        final UpdateCarRequestV1 updateCarRequest =
                new UpdateCarRequestV1(
                        car.getMake(), car.getModel(), car.getColour(), car.getYear());

        // when
        carService.updateCar(car.getId(), updateCarRequest);

        // then
        verify(mockRepository, never()).save(any());
    }

    @Test
    void updateCar_CarNotFound_ExceptionThrown() {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(Optional.empty()).given(mockRepository).findById(id);

        final UpdateCarRequestV1 updateCarRequest =
                new UpdateCarRequestV1("Hyundai", "i30", "Black", 2010);

        // when
        final Executable updateCar = () -> carService.updateCar(id, updateCarRequest);

        // then
        assertThrows(CarNotFoundException.class, updateCar);

        verify(mockRepository, never()).save(any());
    }

    @Test
    void retrieveCar_HappyPath_CarReturned() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Hyundai", "i20", "Red", 2004);
        willReturn(Optional.of(car)).given(mockRepository).findById(car.getId());

        final Collection<SoundsLikeResponseV1> homophones =
                List.of(
                        new SoundsLikeResponseV1("eh", 95, 1),
                        new SoundsLikeResponseV1("uhh", 95, 1));
        willReturn(homophones).given(mockDatamuseRestApi).soundsLike(car.getModel());

        // when
        final Optional<CarDto> retrievedCar = carService.retrieveCar(car.getId());

        // then
        assertTrue(retrievedCar.isPresent());

        final CarDto carDto = retrievedCar.get();
        assertEquals(car.getId(), carDto.getId());
        assertEquals(car.getMake(), carDto.getMake());
        assertEquals(new ModelDto(car.getModel(), "eh, uhh"), carDto.getModel());
        assertEquals(car.getColour(), carDto.getColour());
        assertEquals(car.getYear(), carDto.getYear());
    }

    @Test
    void retrieveCar_CarNotFound_CarReturned() {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(Optional.empty()).given(mockRepository).findById(id);

        // when
        final Optional<CarDto> retrievedCar = carService.retrieveCar(id);

        // then
        assertTrue(retrievedCar.isEmpty());
    }

    @Test
    void deleteCar_HappyPath_CarDeleted() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        carService.deleteCar(id);

        // then
        verify(mockRepository).deleteById(id);
    }
}
