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
 * A TeamInSeason.
 */
@Table("team_in_season")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "teaminseason")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamInSeason implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "team", "game" }, allowSetters = true)
    private Set<TeamInGame> teamInGames = new HashSet<>();

    @Transient
    //@JsonIgnoreProperties(value = { "teamInSeasons" }, allowSetters = true)
    private Team team;

    @Transient
    //@JsonIgnoreProperties(value = { "teamInSeasons", "games" }, allowSetters = true)
    private Season season;

    @Transient
    //@JsonIgnoreProperties(value = { "player", "teamInSeason" }, allowSetters = true)
    private Set<PlayerInTeam> playerInTeams = new HashSet<>();

    @Column("team_id")
    private Long teamId;

    @Column("season_id")
    private Long seasonId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamInSeason id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TeamInGame> getTeamInGames() {
        return this.teamInGames;
    }

    public void setTeamInGames(Set<TeamInGame> teamInGames) {
        if (this.teamInGames != null) {
            this.teamInGames.forEach(i -> i.setTeam(null));
        }
        if (teamInGames != null) {
            teamInGames.forEach(i -> i.setTeam(this));
        }
        this.teamInGames = teamInGames;
    }

    public TeamInSeason teamInGames(Set<TeamInGame> teamInGames) {
        this.setTeamInGames(teamInGames);
        return this;
    }

    public TeamInSeason addTeamInGame(TeamInGame teamInGame) {
        this.teamInGames.add(teamInGame);
        teamInGame.setTeam(this);
        return this;
    }

    public TeamInSeason removeTeamInGame(TeamInGame teamInGame) {
        this.teamInGames.remove(teamInGame);
        teamInGame.setTeam(null);
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
        this.teamId = team != null ? team.getId() : null;
    }

    public TeamInSeason team(Team team) {
        this.setTeam(team);
        return this;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        this.season = season;
        this.seasonId = season != null ? season.getId() : null;
    }

    public TeamInSeason season(Season season) {
        this.setSeason(season);
        return this;
    }

    public Set<PlayerInTeam> getPlayerInTeams() {
        return this.playerInTeams;
    }

    public void setPlayerInTeams(Set<PlayerInTeam> playerInTeams) {
        if (this.playerInTeams != null) {
            this.playerInTeams.forEach(i -> i.setTeamInSeason(null));
        }
        if (playerInTeams != null) {
            playerInTeams.forEach(i -> i.setTeamInSeason(this));
        }
        this.playerInTeams = playerInTeams;
    }

    public TeamInSeason playerInTeams(Set<PlayerInTeam> playerInTeams) {
        this.setPlayerInTeams(playerInTeams);
        return this;
    }

    public TeamInSeason addPlayerInTeam(PlayerInTeam playerInTeam) {
        this.playerInTeams.add(playerInTeam);
        playerInTeam.setTeamInSeason(this);
        return this;
    }

    public TeamInSeason removePlayerInTeam(PlayerInTeam playerInTeam) {
        this.playerInTeams.remove(playerInTeam);
        playerInTeam.setTeamInSeason(null);
        return this;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(Long team) {
        this.teamId = team;
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
        if (!(o instanceof TeamInSeason)) {
            return false;
        }
        return getId() != null && getId().equals(((TeamInSeason) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamInSeason{" +
            "id=" + getId() +
            "}";
    }
}
