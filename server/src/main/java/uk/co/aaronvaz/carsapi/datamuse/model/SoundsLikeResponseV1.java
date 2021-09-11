package uk.co.aaronvaz.carsapi.datamuse.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class SoundsLikeResponseV1 {
    private final String word;

    private final int score;

    private final int numSyllables;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SoundsLikeResponseV1(
            @JsonProperty("word") final String word,
            @JsonProperty("score") final int score,
            @JsonProperty("numSyllables") final int numSyllables) {
        this.word = word;
        this.score = score;
        this.numSyllables = numSyllables;
    }

    public String getWord() {
        return word;
    }

    public int getScore() {
        return score;
    }

    public int getNumSyllables() {
        return numSyllables;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SoundsLikeResponseV1 that = (SoundsLikeResponseV1) o;
        return score == that.score
                && numSyllables == that.numSyllables
                && Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, score, numSyllables);
    }
}
