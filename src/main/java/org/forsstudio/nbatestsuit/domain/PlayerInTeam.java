package org.forsstudio.nbatestsuit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PlayerInTeam.
 */
@Table("player_in_team")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "playerinteam")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerInTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "playerInTeams" }, allowSetters = true)
    private Player player;

    @Transient
    @JsonIgnoreProperties(value = { "teamInGames", "team", "season", "playerInTeams" }, allowSetters = true)
    private TeamInSeason teamInSeason;

    @Column("player_id")
    private Long playerId;

    @Column("team_in_season_id")
    private Long teamInSeasonId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlayerInTeam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.playerId = player != null ? player.getId() : null;
    }

    public PlayerInTeam player(Player player) {
        this.setPlayer(player);
        return this;
    }

    public TeamInSeason getTeamInSeason() {
        return this.teamInSeason;
    }

    public void setTeamInSeason(TeamInSeason teamInSeason) {
        this.teamInSeason = teamInSeason;
        this.teamInSeasonId = teamInSeason != null ? teamInSeason.getId() : null;
    }

    public PlayerInTeam teamInSeason(TeamInSeason teamInSeason) {
        this.setTeamInSeason(teamInSeason);
        return this;
    }

    public Long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(Long player) {
        this.playerId = player;
    }

    public Long getTeamInSeasonId() {
        return this.teamInSeasonId;
    }

    public void setTeamInSeasonId(Long teamInSeason) {
        this.teamInSeasonId = teamInSeason;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerInTeam)) {
            return false;
        }
        return getId() != null && getId().equals(((PlayerInTeam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerInTeam{" +
            "id=" + getId() +
            "}";
    }
}
