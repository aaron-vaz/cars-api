package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.co.aaronvaz.carsapi.datamuse.DatamuseRestApi;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.api.ModelDto;
import uk.co.aaronvaz.carsapi.model.db.Car;

class CarServiceTest {

    private final CarRepository mockRepository = mock(CarRepository.class);

    private final DatamuseRestApi mockDatamuseRestApi = mock(DatamuseRestApi.class);

    private final CarService carService = new CarService(mockRepository, mockDatamuseRestApi);

    @Test
    void addCar_HappyPath_CarAddedToDb() {
        // given
        final CreateOrUpdateCarRequestV1 request =
                new CreateOrUpdateCarRequestV1("BMW", "i8", "Silver", 2015);

        willAnswer(invocation -> invocation.getArgument(0)).given(mockRepository).save(any());

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
