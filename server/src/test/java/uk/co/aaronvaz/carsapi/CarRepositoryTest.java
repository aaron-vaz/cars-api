package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.co.aaronvaz.carsapi.model.db.Car;

@DataJpaTest
class CarRepositoryTest {

    @Autowired private CarRepository carRepository;

    @Autowired private EntityManager entityManager;

    @Test
    void save_HappyPath_EntitySaved() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Ford", "Fiesta", "Silver", 2008);

        // when
        final Car storedCar = carRepository.save(car);

        // then
        assertEquals(car, storedCar);

        final Car dbCar = entityManager.find(Car.class, car.getId());
        assertEquals(car, dbCar);
    }

    @Test
    void findById_HappyPath_EntityReturned() {
        // given
        final Car car = new Car(UUID.randomUUID(), "Audi", "R8", "Silver", 2010);
        entityManager.persist(car);

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
        entityManager.persist(car);

        // when
        carRepository.deleteById(car.getId());

        // then
        final Car dbCar = entityManager.find(Car.class, car.getId());
        assertNull(dbCar);
    }

    @Test
    void findByMake_HappyPath_EntityFound() {
        // given
        final Car seat = new Car(UUID.randomUUID(), "Seat", "Ibiza", "Black", 2020);
        final Car ford = new Car(UUID.randomUUID(), "Ford", "Fiesta", "Black", 2020);
        entityManager.persist(seat);
        entityManager.persist(ford);

        // when
        final Collection<Car> carsByMake = carRepository.findByMake("Ford");

        // then
        assertIterableEquals(List.of(ford), carsByMake);
    }

    @Test
    void findByMakeAAndModel_HappyPath_EntityFound() {
        // given
        final Car ibiza = new Car(UUID.randomUUID(), "Seat", "Ibiza", "Black", 2020);
        final Car leon = new Car(UUID.randomUUID(), "Seat", "Leon", "Black", 2020);
        entityManager.persist(ibiza);
        entityManager.persist(leon);

        // when
        final Collection<Car> carsByMake = carRepository.findByMakeAAndModel("Seat", "Leon");

        // then
        assertIterableEquals(List.of(leon), carsByMake);
    }
}
