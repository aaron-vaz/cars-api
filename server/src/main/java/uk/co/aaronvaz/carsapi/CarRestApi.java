package uk.co.aaronvaz.carsapi;

import java.net.URI;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.CreateOrUpdateCarRequestV1;

@Validated
@RestController
@RequestMapping("/api/v1/car")
class CarRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarRestApi.class);

    private final CarService service;

    CarRestApi(final CarService service) {
        this.service = service;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> add(@Valid @RequestBody final CreateOrUpdateCarRequestV1 request) {
        final CarDto carDto = service.addCar(request);
        final URI carLocation =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(carDto.getId())
                        .toUri();

        return ResponseEntity.created(carLocation).build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CarDto> retrieve(@Valid @PathVariable final UUID id) {
        return ResponseEntity.of(service.retrieveCar(id));
    }

    @DeleteMapping("/{id}")
    void delete(@Valid @PathVariable final UUID id) {
        service.deleteCar(id);
    }

    // Exception Handlers

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
