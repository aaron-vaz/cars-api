package uk.co.aaronvaz.carsapi.datamuse;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;

class DatamuseRestApiTest {
    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);

    private final DatamuseRestApi restApiV1 = new DatamuseRestApi(mockRestTemplate);

    @Test
    void soundLike_HappyPath_ResponseReceived() {
        // given
        final String input = "test";

        final SoundsLikeResponseV1 response = new SoundsLikeResponseV1("test", 100, 1);
        willReturn(new SoundsLikeResponseV1[] {response})
                .given(mockRestTemplate)
                .getForObject("/words?sl=" + input, SoundsLikeResponseV1[].class);

        // when
        final Collection<SoundsLikeResponseV1> responseItems = restApiV1.soundsLike(input);

        // then
        assertIterableEquals(List.of(response), responseItems);
    }

    @Test
    void soundsLike_EmptyBody_EmptyCollection() {
        // given
        final String input = "giraffe";
        willReturn(new SoundsLikeResponseV1[0])
                .given(mockRestTemplate)
                .getForObject("/words?sl=" + input, SoundsLikeResponseV1[].class);

        // when
        final Collection<SoundsLikeResponseV1> responseItems = restApiV1.soundsLike(input);

        // then
        assertTrue(responseItems.isEmpty());
    }

    @ValueSource(classes = {HttpClientErrorException.class, HttpServerErrorException.class})
    @ParameterizedTest
    void soundsLike_ErrorResponses_EmptyCollection(final Class<? extends Throwable> exception) {
        // given
        final String input = "ball";
        willThrow(exception)
                .given(mockRestTemplate)
                .getForObject("/words?sl=" + input, SoundsLikeResponseV1[].class);

        // when
        final Collection<SoundsLikeResponseV1> responseItems = restApiV1.soundsLike(input);

        // then
        assertTrue(responseItems.isEmpty());
    }
}
