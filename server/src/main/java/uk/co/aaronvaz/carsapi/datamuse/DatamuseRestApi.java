package uk.co.aaronvaz.carsapi.datamuse;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.aaronvaz.carsapi.datamuse.model.SoundsLikeResponseV1;

@Component
public class DatamuseRestApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatamuseRestApi.class);

    private final RestTemplate restTemplate;

    public DatamuseRestApi(@Qualifier("datamuse") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Datamuse api that returns words that sound like the input word provided e.g.
     * https://api.datamuse.com/words?sl=jirraf
     *
     * @param input the word to search homophones for
     * @return SoundsLikeResponseV1 which contains all the words that are homophones of the input
     */
    public Collection<SoundsLikeResponseV1> soundsLike(final String input) {
        final URI uri =
                UriComponentsBuilder.newInstance()
                        .path("/words")
                        .queryParam("sl", input)
                        .build()
                        .toUri();

        try {
            final SoundsLikeResponseV1[] response =
                    restTemplate.getForObject(uri, SoundsLikeResponseV1[].class);

            if (response == null) {
                LOGGER.warn("Empty response received from {}", uri);
                return List.of();
            }

            return List.of(response);
        } catch (final HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.warn("Error response received from {}, message: {}", uri, e.getMessage());
            return List.of();
        }
    }
}
