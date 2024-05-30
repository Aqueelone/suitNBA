package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.TeamInSeason} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamInSeasonDTO implements Serializable {

    private Long id;

    private TeamDTO team;

    private SeasonDTO season;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
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
        if (!(o instanceof TeamInSeasonDTO)) {
            return false;
        }

        TeamInSeasonDTO teamInSeasonDTO = (TeamInSeasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamInSeasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamInSeasonDTO{" +
            "id=" + getId() +
            ", team=" + getTeam() +
            ", season=" + getSeason() +
            "}";
    }
}
