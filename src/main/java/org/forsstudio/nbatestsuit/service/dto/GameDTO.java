package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.Game} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GameDTO implements Serializable {

    private Long id;

    private SeasonDTO season;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SeasonDTO getSeason() {
        return season;
    }

    public void setSeason(SeasonDTO season) {
        this.season = season;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameDTO)) {
            return false;
        }

        GameDTO gameDTO = (GameDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gameDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameDTO{" +
            "id=" + getId() +
            ", season=" + getSeason() +
            "}";
    }
}
