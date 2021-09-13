package uk.co.aaronvaz.carsapi.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.aaronvaz.carsapi.CarsApiApplication;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWireMock(port = 0)
@AutoConfigureTestDatabase
@SpringBootTest(
        classes = CarsApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarsRestApiV1IntegrationTest {

    @Autowired private TestRestTemplate testRestTemplate;

    private static Map<String, Object> createRequest() {
        return Map.of("make", "Ford", "model", "Focus", "colour", "Black", "year", 2020);
    }

    private static Map<String, Object> updateRequest() {
        return Map.of("make", "Ford", "model", "Fiesta", "colour", "Blue", "year", 2020);
    }

    private static Map<String, Object> partialUpdateRequest() {
        return Map.of("make", "Fiesta");
    }

    private static String jsonObjectFromRequest(final String id, final Map<String, Object> request)
            throws JSONException {
        final JSONObject modelObject =
                new JSONObject().put("name", request.get("model")).put("homophones", "");

        return new JSONObject()
                .put("id", id)
                .put("make", request.get("make"))
                .put("model", modelObject)
                .put("colour", request.get("colour"))
                .put("year", request.get("year"))
                .toString();
    }

    private static String jsonArrayFromRequest(final String id, final Map<String, Object> request)
            throws JSONException {
        return "[" + jsonObjectFromRequest(id, request) + "]";
    }

    private static String getIdFromLocation(final URI retrieveLocation) {
        final String path = retrieveLocation.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Test
    void add_HappyPath_201Created() {
        // given
        final Map<String, Object> request = createRequest();

        // when
        final ResponseEntity<Void> response =
                testRestTemplate.postForEntity("/api/v1/cars", request, Void.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    void add_InvalidRequest_400BadRequest() {
        // given
        final Map<String, String> request = Map.of("make", "Ford");

        // when
        final ResponseEntity<Void> response =
                testRestTemplate.postForEntity("/api/v1/cars", request, Void.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getHeaders().containsKey(HttpHeaders.LOCATION));
    }

    @Test
    void retrieve_HappyPath_200Ok() throws JSONException {
        // given
        final Map<String, Object> request = createRequest();
        final URI retrieveLocation = testRestTemplate.postForLocation("/api/v1/cars", request);

        // when
        final ResponseEntity<String> responseEntity =
                testRestTemplate.getForEntity(retrieveLocation, String.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final String id = getIdFromLocation(retrieveLocation);
        final String expectedBody = jsonObjectFromRequest(id, request);

        JSONAssert.assertEquals(expectedBody, responseEntity.getBody(), true);
    }

    @Test
    void retrieve_NoCarFound_404NotFound() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        final ResponseEntity<Void> responseEntity =
                testRestTemplate.getForEntity("/api/v1/cars/" + id, Void.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void update_HappyPath_204NoContent() throws JSONException {
        // given
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final Map<String, Object> updateRequest = updateRequest();
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(updateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PUT, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        final ResponseEntity<String> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, String.class);
        final String expectedBody = jsonObjectFromRequest(id, updateRequest);

        JSONAssert.assertEquals(expectedBody, storedCar.getBody(), true);
    }

    @Test
    void update_CarDoesntExist_201Created() throws JSONException {
        // given
        final String id = UUID.randomUUID().toString();
        final Map<String, Object> updateRequest = updateRequest();

        // when
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(updateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PUT, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final URI retrieveLocation = response.getHeaders().getLocation();
        final ResponseEntity<String> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, String.class);

        final String expectedBody = jsonObjectFromRequest(id, updateRequest);

        JSONAssert.assertEquals(expectedBody, storedCar.getBody(), true);
    }

    @Test
    void update_InvalidJson_400BadRequest() {
        // given
        final String id = UUID.randomUUID().toString();
        final Map<String, Object> updateRequest = Map.of("year", 2020);

        // when
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(updateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PUT, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void partialUpdate_HappyPath_204NoContent() throws JSONException {
        // given
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final Map<String, Object> partialUpdateRequest = partialUpdateRequest();
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(partialUpdateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PATCH, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        final ResponseEntity<String> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, String.class);

        final HashMap<String, Object> tmpRequest = new HashMap<>(createRequest);
        tmpRequest.putAll(partialUpdateRequest);

        final String expectedBody = jsonObjectFromRequest(id, tmpRequest);

        JSONAssert.assertEquals(expectedBody, storedCar.getBody(), true);
    }

    @Test
    void partialUpdate_FullUpdate_204NoContent() throws JSONException {
        // given
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final Map<String, Object> updateRequest = updateRequest();
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(updateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PATCH, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        final ResponseEntity<String> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, String.class);
        final String expectedBody = jsonObjectFromRequest(id, updateRequest);

        JSONAssert.assertEquals(expectedBody, storedCar.getBody(), true);
    }

    @Test
    void partialUpdate_NoChangeUpdated_204NoContent() throws JSONException {
        // given
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(createRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PATCH, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        final ResponseEntity<String> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, String.class);
        final String expectedBody = jsonObjectFromRequest(id, createRequest);

        JSONAssert.assertEquals(expectedBody, storedCar.getBody(), true);
    }

    @Test
    void partialUpdate_CarNotFound_404NotFound() {
        // given
        final String id = UUID.randomUUID().toString();
        final Map<String, Object> partialUpdateRequest = partialUpdateRequest();

        // when
        final HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(partialUpdateRequest);

        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.PATCH, httpEntity, Void.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void delete_HappyPath_200Ok() {
        // given
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.DELETE, null, Void.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final ResponseEntity<Void> storedCar =
                testRestTemplate.getForEntity(retrieveLocation, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, storedCar.getStatusCode());
    }

    @Test
    void delete_CarNotFound_404NotFound() {
        // given
        final String id = UUID.randomUUID().toString();

        // when
        final ResponseEntity<Void> response =
                testRestTemplate.exchange(
                        "/api/v1/cars/" + id, HttpMethod.DELETE, null, Void.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void findByMake_HappyPath_200Ok() throws JSONException {
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final ResponseEntity<String> response =
                testRestTemplate.getForEntity(
                        "/api/v1/cars/make/" + createRequest.get("make"), String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final String expectedBody = jsonArrayFromRequest(id, createRequest);
        JSONAssert.assertEquals(expectedBody, response.getBody(), true);
    }

    @Test
    void findByMake_NoMatches_200Ok() throws JSONException {
        final String make = "Nissan";

        // when
        final ResponseEntity<String> response =
                testRestTemplate.getForEntity("/api/v1/cars/make/" + make, String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JSONAssert.assertEquals("[]", response.getBody(), true);
    }

    @Test
    void findByMakeAndModel_HappyPath_200Ok() throws JSONException {
        final Map<String, Object> createRequest = createRequest();
        final URI retrieveLocation =
                testRestTemplate.postForLocation("/api/v1/cars", createRequest);

        final String id = getIdFromLocation(retrieveLocation);

        // when
        final String url =
                "/api/v1/cars/make/"
                        + createRequest.get("make")
                        + "/model/"
                        + createRequest.get("model");
        final ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final String expectedBody = jsonArrayFromRequest(id, createRequest);
        JSONAssert.assertEquals(expectedBody, response.getBody(), true);
    }

    @Test
    void findByMakeAndModel_NoMatches_200Ok() throws JSONException {
        final String make = "Nissan";
        final String model = "Juke";

        // when
        final ResponseEntity<String> response =
                testRestTemplate.getForEntity(
                        "/api/v1/cars/make/" + make + "/model/" + model, String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JSONAssert.assertEquals("[]", response.getBody(), true);
    }
}
