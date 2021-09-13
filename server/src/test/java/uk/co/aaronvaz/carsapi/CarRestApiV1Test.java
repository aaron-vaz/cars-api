package uk.co.aaronvaz.carsapi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import uk.co.aaronvaz.carsapi.model.api.CarDto;
import uk.co.aaronvaz.carsapi.model.api.ModelDto;
import uk.co.aaronvaz.carsapi.model.api.PartialUpdateCarRequestV1;

@WebMvcTest(CarRestApiV1.class)
class CarRestApiV1Test {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CarService mockCarService;

    @Test
    void add_HappyPath_201Created() throws Exception {
        // given
        final CarDto carDto =
                new CarDto(UUID.randomUUID(), "Ford", new ModelDto("Focus", ""), "Blue", 2010);
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
                        post("/api/v1/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        final MvcResult mvcResult = actions.andExpect(status().isCreated()).andReturn();
        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        assertNotNull(locationHeader);
        assertTrue(locationHeader.endsWith("/api/v1/cars/" + carDto.getId()));

        verify(mockCarService).addCar(any());
    }

    @Test
    void add_InvalidJson_400BadRequest() throws Exception {
        // given
        final String request = "{ \"make\": \"Ford\" }";

        // when
        final ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/cars")
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
                        post("/api/v1/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        actions.andExpect(status().isInternalServerError());
    }

    @Test
    void update_HappyPath_204NoContent() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(false).given(mockCarService).updateCar(eq(id), any());

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
                        put("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        actions.andExpect(status().isNoContent());
    }

    @Test
    void update_CarDoesntExistAndWasCreated_201Created() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(true).given(mockCarService).updateCar(eq(id), any());

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
                        put("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        final MvcResult mvcResult = actions.andExpect(status().isCreated()).andReturn();
        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        assertNotNull(locationHeader);
        assertTrue(locationHeader.endsWith("/api/v1/cars/" + id));
    }

    @Test
    void partialUpdate_InvalidJson_400BadRequest() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        final String request = "{ \"year\": 2000 }";

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        put("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).updateCar(any(), any());
    }

    @Test
    void update_InvalidId_400BadRequest() throws Exception {
        // given
        final String invalidId = "invalid";
        final String request =
                "{\n"
                        + "  \"make\": \"Ford\",\n"
                        + "  \"model\": \"Focus\",\n"
                        + "  \"colour\": \"Blue\",\n"
                        + "  \"year\": 2010\n"
                        + "}";

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        put("/api/v1/cars/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).updateCar(any(), any());
    }

    @Test
    void update_UncheckedException_500ServerError() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        final String request =
                "{\n"
                        + "  \"make\": \"Ford\",\n"
                        + "  \"model\": \"Focus\",\n"
                        + "  \"colour\": \"Blue\",\n"
                        + "  \"year\": 2010\n"
                        + "}";

        willThrow(RuntimeException.class).given(mockCarService).updateCar(eq(id), any());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        put("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @ValueSource(
            strings = {
                "{ \"make\": \"Ford\", \"model\": \"Kuga\", \"colour\": \"Green\", \"year\": 2020 }",
                "{ \"year\": 2010 }"
            })
    @ParameterizedTest
    void partialUpdate_FullOrPartialUpdate_204NoContent(final String request) throws Exception {
        // given
        final UUID id = UUID.randomUUID();

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        patch("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isNoContent());

        verify(mockCarService)
                .partialUpdateCar(
                        id, objectMapper.readValue(request, PartialUpdateCarRequestV1.class));
    }

    @Test
    void partialUpdate_CarNotFound_404NotFound() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        final String request = "{ \"year\": 2000 }";

        willThrow(CarNotFoundException.class).given(mockCarService).partialUpdateCar(eq(id), any());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        patch("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void partialUpdate_InvalidId_400BadRequest() throws Exception {
        // given
        final String invalidId = "invalid";
        final String request = "{ \"year\": 2000 }";

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        patch("/api/v1/cars/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).partialUpdateCar(any(), any());
    }

    @Test
    void partialUpdate_UncheckedException_500ServerError() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        final String request = "{ \"year\": 2000 }";

        willThrow(RuntimeException.class).given(mockCarService).partialUpdateCar(eq(id), any());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        patch("/api/v1/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void retrieve_HappyPath_200Ok() throws Exception {
        // given
        final CarDto carDto =
                new CarDto(UUID.randomUUID(), "Nissan", new ModelDto("Micra", ""), "Blue", 2012);
        willReturn(Optional.of(carDto)).given(mockCarService).retrieveCar(carDto.getId());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/{id}", carDto.getId())
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carDto)));
    }

    @Test
    void retrieve_CarNotFound_404NotFound() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willReturn(Optional.empty()).given(mockCarService).retrieveCar(id);

        // when
        final ResultActions resultActions =
                mockMvc.perform(get("/api/v1/cars/{id}", id).accept(MediaType.APPLICATION_JSON));

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
                        get("/api/v1/cars/{id}", invalidId).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).retrieveCar(any());
    }

    @Test
    void retrieve_UncheckException_500ServerError() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willThrow(RuntimeException.class).given(mockCarService).retrieveCar(id);

        // when
        final ResultActions resultActions =
                mockMvc.perform(get("/api/v1/cars/{id}", id).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void delete_HappyPath_200Ok() throws Exception {
        // given
        final UUID id = UUID.randomUUID();

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/cars/{id}", id));

        // then
        resultActions.andExpect(status().isOk());

        verify(mockCarService).deleteCar(id);
    }

    @Test
    void delete_InvalidId_400BadRequest() throws Exception {
        // given
        final String invalidId = "invalid";

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/cars/{id}", invalidId));

        // then
        resultActions.andExpect(status().isBadRequest());

        verify(mockCarService, never()).deleteCar(any());
    }

    @Test
    void delete_UncheckedError_500ServerError() throws Exception {
        // given
        final UUID id = UUID.randomUUID();
        willThrow(RuntimeException.class).given(mockCarService).deleteCar(id);

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/cars/{id}", id));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void retrieveByMake_HappyPath_200Ok() throws Exception {
        // given
        final CarDto carDto =
                new CarDto(UUID.randomUUID(), "Nissan", new ModelDto("Juke", ""), "Blue", 2012);
        willReturn(List.of(carDto)).given(mockCarService).findCarsByMake(carDto.getMake());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/make/{make}", carDto.getMake())
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(carDto))));
    }

    @Test
    void retrieveByMake_NoMatches_200OkWithEmptyArrayJson() throws Exception {
        // given
        final String make = "Aston Martin";
        willReturn(List.of()).given(mockCarService).findCarsByMake(make);

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/make/{make}", make).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void retrieveByMake_UncheckedError_500OkServerError() throws Exception {
        // given
        final String make = "Volvo";
        willThrow(RuntimeException.class).given(mockCarService).findCarsByMake(make);

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/make/{make}", make).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void retrieveByMakeAndModel_HappyPath_200Ok() throws Exception {
        // given
        final CarDto carDto =
                new CarDto(UUID.randomUUID(), "VW", new ModelDto("Golf", ""), "Blue", 2012);
        willReturn(List.of(carDto))
                .given(mockCarService)
                .findCarsByMakeAndModel(carDto.getMake(), carDto.getModel().getModel());

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get(
                                        "/api/v1/cars/make/{make}/model/{model}",
                                        carDto.getMake(),
                                        carDto.getModel().getModel())
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(carDto))));
    }

    @Test
    void retrieveByMakeAndModel_NoMatches_200OkWithEmptyArrayJson() throws Exception {
        // given
        final String make = "VW";
        final String model = "Polo";
        willReturn(List.of()).given(mockCarService).findCarsByMakeAndModel(make, model);

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/make/{make}/model/{model}", make, model)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void retrieveByMakeAndModel_UncheckedError_500OkServerError() throws Exception {
        // given
        final String make = "Kia";
        final String model = "Rio";
        willThrow(RuntimeException.class).given(mockCarService).findCarsByMakeAndModel(make, model);

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/cars/make/{make}/model/{model}", make, model)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }
}
