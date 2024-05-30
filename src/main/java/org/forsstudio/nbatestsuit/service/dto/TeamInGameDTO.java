package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.TeamInGame} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamInGameDTO implements Serializable {

    private Long id;

    private TeamInSeasonDTO team;

    private GameDTO game;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamInSeasonDTO getTeam() {
        return team;
    }

    public void setTeam(TeamInSeasonDTO team) {
        this.team = team;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamInGameDTO)) {
            return false;
        }

        TeamInGameDTO teamInGameDTO = (TeamInGameDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamInGameDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamInGameDTO{" +
            "id=" + getId() +
            ", team=" + getTeam() +
            ", game=" + getGame() +
            "}";
    }
}
