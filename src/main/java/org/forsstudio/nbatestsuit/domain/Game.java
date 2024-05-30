package org.forsstudio.nbatestsuit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Game.
 */
@Table("game")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "game")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "team", "player", "game" }, allowSetters = true)
    private Set<PlayerInGame> playerInGames = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "team", "game" }, allowSetters = true)
    private Set<TeamInGame> teamInGames = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "teamInSeasons", "games" }, allowSetters = true)
    private Season season;

    @Column("season_id")
    private Long seasonId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Game id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<PlayerInGame> getPlayerInGames() {
        return this.playerInGames;
    }

    public void setPlayerInGames(Set<PlayerInGame> playerInGames) {
        if (this.playerInGames != null) {
            this.playerInGames.forEach(i -> i.setGame(null));
        }
        if (playerInGames != null) {
            playerInGames.forEach(i -> i.setGame(this));
        }
        this.playerInGames = playerInGames;
    }

    public Game playerInGames(Set<PlayerInGame> playerInGames) {
        this.setPlayerInGames(playerInGames);
        return this;
    }

    public Game addPlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.add(playerInGame);
        playerInGame.setGame(this);
        return this;
    }

    public Game removePlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.remove(playerInGame);
        playerInGame.setGame(null);
        return this;
    }

    public Set<TeamInGame> getTeamInGames() {
        return this.teamInGames;
    }

    public void setTeamInGames(Set<TeamInGame> teamInGames) {
        if (this.teamInGames != null) {
            this.teamInGames.forEach(i -> i.setGame(null));
        }
        if (teamInGames != null) {
            teamInGames.forEach(i -> i.setGame(this));
        }
        this.teamInGames = teamInGames;
    }

    public Game teamInGames(Set<TeamInGame> teamInGames) {
        this.setTeamInGames(teamInGames);
        return this;
    }

    public Game addTeamInGame(TeamInGame teamInGame) {
        this.teamInGames.add(teamInGame);
        teamInGame.setGame(this);
        return this;
    }

    public Game removeTeamInGame(TeamInGame teamInGame) {
        this.teamInGames.remove(teamInGame);
        teamInGame.setGame(null);
        return this;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        this.season = season;
        this.seasonId = season != null ? season.getId() : null;
    }

    public Game season(Season season) {
        this.setSeason(season);
        return this;
    }

    public Long getSeasonId() {
        return this.seasonId;
    }

    public void setSeasonId(Long season) {
        this.seasonId = season;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Game)) {
            return false;
        }
        return getId() != null && getId().equals(((Game) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Game{" +
            "id=" + getId() +
            "}";
    }
}
