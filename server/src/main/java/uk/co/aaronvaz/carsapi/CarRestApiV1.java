package uk.co.aaronvaz.carsapi;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;
import uk.co.aaronvaz.carsapi.model.api.PartialUpdateCarRequestV1;

@Validated
@RestController
@RequestMapping("/api/v1/cars")
class CarRestApiV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarRestApiV1.class);

    private final CarService service;

    CarRestApiV1(final CarService service) {
        this.service = service;
    }

    /**
     * Add a Car
     *
     * <p>Request:
     *
     * <pre>
     *     POST /api/v1/cars
     *     Content-Type: application/json
     *
     *     {
     *      "make": "Ford",
     *      "model": "Focus",
     *      "colour": "Blue",
     *      "year": 2010
     *     }
     * </pre>
     *
     * <p>Response:
     *
     * <pre>
     *     HTTP 201 Created
     *     Location: /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     * </pre>
     *
     * @param request the request body
     * @return 201 if the Car was successfully created
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> add(@Valid @RequestBody final CreateOrUpdateCarRequestV1 request) {
        final CarDto carDto = service.addCar(request);
        final URI carLocation = retrieveCarUri(carDto.getId());

        return ResponseEntity.created(carLocation).build();
    }

    /**
     * Update an existing Car
     *
     * <p>Request:
     *
     * <pre>
     *     PUT /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     *     Content-Type: application/json
     *
     *     {
     *      "make": "Ford",
     *      "model": "Focus",
     *      "colour": "Blue",
     *      "year": 2010
     *     }
     * </pre>
     *
     * <p>Response if successfully updated:
     *
     * <pre>
     *     HTTP 204 No Content
     * </pre>
     *
     * <p>Response if Car was not found and so was created:
     *
     * <pre>
     *     HTTP 201 Created
     *     Location: /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     * </pre>
     *
     * @param request the update request body
     * @return 204 if the Car was successfully updated or 201 if the car was not found and created
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> update(
            @PathVariable final UUID id,
            @Valid @RequestBody final CreateOrUpdateCarRequestV1 request) {
        final boolean created = service.updateCar(id, request);
        return created
                ? ResponseEntity.created(retrieveCarUri(id)).build()
                : ResponseEntity.noContent().build();
    }

    /**
     * Update an existing Car. Also allows partial updates
     *
     * <p>Full update Request:
     *
     * <pre>
     *     PATCH /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     *     Content-Type: application/json
     *
     *     {
     *      "make": "Ford",
     *      "model": "Focus",
     *      "colour": "Blue",
     *      "year": 2010
     *     }
     * </pre>
     *
     * <p>Partial update Request:
     *
     * <pre>
     *     PATCH /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     *     Content-Type: application/json
     *
     *     {
     *      "make": "Ford"
     *     }
     * </pre>
     *
     * <p>Response if successfully updated:
     *
     * <pre>
     *     HTTP 204 No Content
     * </pre>
     *
     * <p>Response if Car was not found:
     *
     * <pre>
     *     HTTP 404 Not Found
     * </pre>
     *
     * @param request the update request body
     * @return 204 if the Car was successfully updated or 404 if car was not found
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> partialUpdate(
            @PathVariable final UUID id, @RequestBody final PartialUpdateCarRequestV1 request)
            throws CarNotFoundException {
        service.partialUpdateCar(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a Car
     *
     * <p>Request:
     *
     * <pre>
     *     GET /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     *     Accept: application/json
     * </pre>
     *
     * <p>Response if successful:
     *
     * <pre>
     *     HTTP 200 OK
     *     Content-Type: application/json
     *
     *     {
     *      "make": "Ford",
     *      "model": {
     *          "name": "Focus",
     *          "homophones": "focus, fokus, phocus, ficus, focas"
     *      },
     *      "colour": "Blue",
     *      "year": 2010
     *     }
     * </pre>
     *
     * <p>Response if car was not found:
     *
     * <pre>
     *     HTTP 404 Not Found
     * </pre>
     *
     * @param id the id of the stored car
     * @return 200 with the Car properties in json or 404 if car doesn't exist
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CarDto> retrieve(@PathVariable final UUID id) {
        return ResponseEntity.of(service.retrieveCar(id));
    }

    /**
     * Delete a Car
     *
     * <p>Request:
     *
     * <pre>
     *     DELETE /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
     * </pre>
     *
     * <p>Response if successful:
     *
     * <pre>
     *     HTTP 200 OK
     * </pre>
     *
     * Response if Car not found
     *
     * <pre>
     *     HTTP 404 Not Found
     * </pre>
     *
     * @param id the id of the stored car
     */
    @DeleteMapping("/{id}")
    void delete(@PathVariable final UUID id) throws CarNotFoundException {
        service.deleteCar(id);
    }

    /**
     * Retrieve a Car by make
     *
     * <p>Request:
     *
     * <pre>
     *     GET /api/v1/cars/make/Ford
     *     Accept: application/json
     * </pre>
     *
     * <p>Response if successful:
     *
     * <pre>
     *     HTTP 200 OK
     *     Content-Type: application/json
     *
     *     [
     *      {
     *          "make": "Ford",
     *          "model": {
     *              "name": "Focus",
     *              "homophones": "focus, fokus, phocus, ficus, focas"
     *          },
     *          "colour": "Blue",
     *          "year": 2010
     *      }
     *     ]
     * </pre>
     *
     * <p>Response if no matches were not found:
     *
     * <pre>
     *     HTTP 200 OK
     *     Content-type: application/json
     *
     *     []
     * </pre>
     *
     * @param make the make of the Car to search for
     * @return 200 and json array with cars or 200 with empty json array
     */
    @GetMapping(value = "/make/{make}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Collection<CarDto>> retrieveByMake(@PathVariable final String make) {
        return ResponseEntity.ok(service.findCarsByMake(make));
    }

    /**
     * Retrieve a Car by make & model
     *
     * <p>Request:
     *
     * <pre>
     *     GET /api/v1/cars/make/Ford/model/Focus
     *     Accept: application/json
     * </pre>
     *
     * <p>Response if successful:
     *
     * <pre>
     *     HTTP 200 OK
     *     Content-Type: application/json
     *
     *     [
     *      {
     *          "make": "Ford",
     *          "model": {
     *              "name": "Focus",
     *              "homophones": "focus, fokus, phocus, ficus, focas"
     *          },
     *          "colour": "Blue",
     *          "year": 2010
     *      }
     *     ]
     * </pre>
     *
     * <p>Response if no matches were not found:
     *
     * <pre>
     *     HTTP 200 OK
     *     Content-type: application/json
     *
     *     []
     * </pre>
     *
     * @param make the make of the Car to search for
     * @param model the model of the car to search for
     * @return 200 and json array with cars or 200 with empty json array
     */
    @GetMapping(value = "/make/{make}/model/{model}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Collection<CarDto>> retrieveByMakeAndModel(
            @PathVariable final String make, @PathVariable final String model) {
        return ResponseEntity.ok(service.findCarsByMakeAndModel(make, model));
    }

    private URI retrieveCarUri(final UUID id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/cars/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    // Exception Handlers

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CarNotFoundException.class)
    void handleCarNotFound() {}

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MethodArgumentTypeMismatchException.class
    })
    ResponseEntity<String> handleRequestValidationErrors(final Exception exception) {
        LOGGER.debug("Error processing Cars API request", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleGeneralServerErrors(final Exception exception) {
        LOGGER.warn("Error processing Car Api request", exception);
        return ResponseEntity.internalServerError().build();
    }
}
