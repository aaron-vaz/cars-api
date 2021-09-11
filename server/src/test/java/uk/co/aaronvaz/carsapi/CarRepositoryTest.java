package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import uk.co.aaronvaz.carsapi.model.db.Car;

@SpringBootTest
class CarRepositoryTest {

    @Autowired private CarRepository carRepository;

    @Autowired private KeyValueTemplate keyValueTemplate;

    @Test
    void save_HappyPath_EntitySaved() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Ford", "Fiesta", "Silver", 2008);

        // when
        final Car storedCar = carRepository.save(car);

        // then
        assertEquals(car, storedCar);

        final Optional<Car> dbCar = keyValueTemplate.findById(car.getId(), Car.class);
        assertTrue(dbCar.isPresent());
        assertEquals(car, dbCar.get());
    }

    @Test
    void findById_HappyPath_EntityReturned() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Audi", "R8", "Silver", 2010);
        keyValueTemplate.insert(car);

        // when
        final Optional<Car> storedCar = carRepository.findById(car.getId());

        // then
        assertTrue(storedCar.isPresent());
        assertEquals(car, storedCar.get());
    }

    @Test
    void findById_NoEntityStoredWithId_EmptyOptionalReturned() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        final Optional<Car> storedCar = carRepository.findById(id);

        // then
        assertTrue(storedCar.isEmpty());
    }

    @Test
    void deleteById_HappyPath_EntityDeleted() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Seat", "Ibiza", "Black", 2020);
        keyValueTemplate.insert(car);

        // when
        carRepository.deleteById(car.getId());

        // then
        final Optional<Car> dbCar = keyValueTemplate.findById(car.getId(), Car.class);
        assertTrue(dbCar.isEmpty());
    }
}
