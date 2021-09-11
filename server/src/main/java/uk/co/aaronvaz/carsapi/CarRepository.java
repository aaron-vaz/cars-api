package uk.co.aaronvaz.carsapi;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import uk.co.aaronvaz.carsapi.model.db.Car;

@Repository
interface CarRepository extends org.springframework.data.repository.Repository<Car, UUID> {

    /**
     * Save the {@link Car} entity to the database
     *
     * @param car the car to save
     * @return the saved instance. This will include any auto generated data
     */
    Car save(Car car);

    /**
     * Return a {@link Car} entity that matches the given id from the database
     *
     * @param id the id to look up the entity by
     * @return Optional containing the matched entity or {@link Optional#empty()} if nothing was
     *     found
     */
    Optional<Car> findById(UUID id);

    /**
     * Delete a {@link Car} entity from the database matching the given id
     *
     * @param id the id to delete the entity by
     */
    void deleteById(UUID id);
}
