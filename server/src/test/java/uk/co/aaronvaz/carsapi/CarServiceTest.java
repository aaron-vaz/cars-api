package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.db.Car;

class CarServiceTest {

    private final CarRepository mockRepository = mock(CarRepository.class);

    private final CarService carService = new CarService(mockRepository);

    @Test
    void addCar_HappyPath_CarAddedToDb() {
        // given
        final CreateOrUpdateCarRequestV1 request =
                new CreateOrUpdateCarRequestV1("BMW", "i8", "Silver", 2015);

        willAnswer(invocation -> invocation.getArgument(0)).given(mockRepository).save(any());

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
        assertEquals(request.getModel(), carDto.getModel());
        assertEquals(request.getColour(), carDto.getColour());
        assertEquals(request.getYear(), carDto.getYear());
    }
}
