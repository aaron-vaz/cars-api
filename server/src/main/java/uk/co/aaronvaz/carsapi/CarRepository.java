package uk.co.aaronvaz.carsapi;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Find all {@link Car} entities in the database that have the provided make
     *
     * @param make the make of the car to search for
     * @return {@link Car} entities that contain the supplied make
     */
    @Query("select c from Car c where c.make = :make")
    Collection<Car> findByMake(@Param("make") String make);

    /**
     * Find all {@link Car} entities in the database that have the provided make & model
     *
     * @param make the make of the car to search for
     * @param model the model to search for
     * @return {@link Car} entities that contain the supplied make & model
     */
    @Query("select c from Car c where c.make = :make and c.model = :model")
    Collection<Car> findByMakeAAndModel(@Param("make") String make, @Param("model") String model);
}
