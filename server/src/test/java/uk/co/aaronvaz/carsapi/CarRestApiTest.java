package uk.co.aaronvaz.carsapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CarRestApi.class)
class CarRestApiTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private CarService mockCarService;

    @Test
    void add_HappyPath_201Created() throws Exception {
        // given
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
        actions.andExpect(status().isCreated());

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
}
