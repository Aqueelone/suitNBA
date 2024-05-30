package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.Season} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeasonDTO implements Serializable {

    private Long id;

    private String seasonName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeasonDTO)) {
            return false;
        }

        SeasonDTO seasonDTO = (SeasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, seasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeasonDTO{" +
            "id=" + getId() +
            ", seasonName='" + getSeasonName() + "'" +
            "}";
    }
}
