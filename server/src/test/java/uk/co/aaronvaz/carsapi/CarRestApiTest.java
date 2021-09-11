package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import uk.co.aaronvaz.carsapi.model.api.CarDto;

@WebMvcTest(CarRestApi.class)
class CarRestApiTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CarService mockCarService;

    @Test
    void add_HappyPath_201Created() throws Exception {
        // given
        final CarDto carDto = new CarDto(UUID.randomUUID(), "Ford", "Focus", "Blue", 2010);
        willReturn(carDto).given(mockCarService).addCar(any());

        final String request =
                "{\n"
                        + "  \"make\": \"Ford\",\n"
                        + "  \"model\": \"Focus\",\n"
                        + "  \"colour\": \"Blue\",\n"
                        + "  \"year\": 2010\n"
                        + "}";

        // when
        final ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/car")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        final MvcResult mvcResult = actions.andExpect(status().isCreated()).andReturn();
        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        assertNotNull(locationHeader);
        assertTrue(locationHeader.endsWith("/api/v1/car/" + carDto.getId()));

        verify(mockCarService).addCar(any());
    }

    @Test
    void add_InvalidJson_400BadRequest() throws Exception {
        // given
        final String request = "{ \"make\": \"Ford\" }";

        // when
        final ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/car")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        actions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).addCar(any());
    }

    @Test
    void add_UncheckedError_500ServerError() throws Exception {
        // given
        willThrow(RuntimeException.class).given(mockCarService).addCar(any());

        final String request =
                "{\n"
                        + "  \"make\": \"Ford\",\n"
                        + "  \"model\": \"Focus\",\n"
                        + "  \"colour\": \"Blue\",\n"
                        + "  \"year\": 2010\n"
                        + "}";

        // when
        final ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/car")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        actions.andExpect(status().isInternalServerError());
    }

    @Test
    void retrieve_HappyPath_200Ok() throws Exception {
        // given
        final CarDto carDto = new CarDto(UUID.randomUUID(), "Nissan", "Micra", "Blue", 2012);
        willReturn(Optional.of(carDto)).given(mockCarService).retrieveCar(carDto.getId());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/car/{id}", carDto.getId()).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(carDto)));
    }

    @Test
    void retrieve_CarNotFound_404NotFound() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(Optional.empty()).given(mockCarService).retrieveCar(id);

        // when
        final ResultActions resultActions =
                mockMvc.perform(get("/api/v1/car/{id}", id).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void retrieve_InvalidId_400BadRequest() throws Exception {
        // given
        final String invalidId = "invalid";

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/car/{id}", invalidId).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
