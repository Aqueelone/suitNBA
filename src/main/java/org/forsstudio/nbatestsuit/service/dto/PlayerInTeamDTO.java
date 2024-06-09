package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.PlayerInTeam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerInTeamDTO implements Serializable {

    private Long id;

    private PlayerDTO player;

    private TeamInSeasonDTO teamInSeason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public TeamInSeasonDTO getTeamInSeason() {
        return teamInSeason;
    }

    public void setTeamInSeason(TeamInSeasonDTO teamInSeason) {
        this.teamInSeason = teamInSeason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerInTeamDTO playerInTeamDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, playerInTeamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerInTeamDTO{" +
            "id=" + getId() +
            ", player=" + getPlayer() +
            ", teamInSeason=" + getTeamInSeason() +
            "}";
    }
}
