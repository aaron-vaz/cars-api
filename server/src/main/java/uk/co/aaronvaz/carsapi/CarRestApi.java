package uk.co.aaronvaz.carsapi;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        service.addCar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Exception Handlers

    @ExceptionHandler
    ResponseEntity<String> handleRequestValidationErrors(
            final MethodArgumentNotValidException exception) {
        LOGGER.debug("Error processing Cars API request", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<?> handleGeneralServerErrors(final Exception exception) {
        LOGGER.warn("Error processing Car Api request", exception);
        return ResponseEntity.internalServerError().build();
    }
}
