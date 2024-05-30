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
 * A Season.
 */
@Table("season")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "season")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Season implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("season_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String seasonName;

    @Transient
    @JsonIgnoreProperties(value = { "teamInGames", "team", "season" }, allowSetters = true)
    private Set<TeamInSeason> teamInSeasons = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "teamInGames", "season" }, allowSetters = true)
    private Set<Game> games = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Season id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeasonName() {
        return this.seasonName;
    }

    public Season seasonName(String seasonName) {
        this.setSeasonName(seasonName);
        return this;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Set<TeamInSeason> getTeamInSeasons() {
        return this.teamInSeasons;
    }

    public void setTeamInSeasons(Set<TeamInSeason> teamInSeasons) {
        if (this.teamInSeasons != null) {
            this.teamInSeasons.forEach(i -> i.setSeason(null));
        }
        if (teamInSeasons != null) {
            teamInSeasons.forEach(i -> i.setSeason(this));
        }
        this.teamInSeasons = teamInSeasons;
    }

    public Season teamInSeasons(Set<TeamInSeason> teamInSeasons) {
        this.setTeamInSeasons(teamInSeasons);
        return this;
    }

    public Season addTeamInSeason(TeamInSeason teamInSeason) {
        this.teamInSeasons.add(teamInSeason);
        teamInSeason.setSeason(this);
        return this;
    }

    public Season removeTeamInSeason(TeamInSeason teamInSeason) {
        this.teamInSeasons.remove(teamInSeason);
        teamInSeason.setSeason(null);
        return this;
    }

    public Set<Game> getGames() {
        return this.games;
    }

    public void setGames(Set<Game> games) {
        if (this.games != null) {
            this.games.forEach(i -> i.setSeason(null));
        }
        if (games != null) {
            games.forEach(i -> i.setSeason(this));
        }
        this.games = games;
    }

    public Season games(Set<Game> games) {
        this.setGames(games);
        return this;
    }

    public Season addGame(Game game) {
        this.games.add(game);
        game.setSeason(this);
        return this;
    }

    public Season removeGame(Game game) {
        this.games.remove(game);
        game.setSeason(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Season)) {
            return false;
        }
        return getId() != null && getId().equals(((Season) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Season{" +
            "id=" + getId() +
            ", seasonName='" + getSeasonName() + "'" +
            "}";
    }
}
