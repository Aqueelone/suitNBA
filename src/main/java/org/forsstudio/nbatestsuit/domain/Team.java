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
 * A Team.
 */
@Table("team")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "team")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("team_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String teamName;

    @Transient
    @JsonIgnoreProperties(value = { "teamInGames", "team", "season" }, allowSetters = true)
    private Set<TeamInSeason> teamInSeasons = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public Team teamName(String teamName) {
        this.setTeamName(teamName);
        return this;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Set<TeamInSeason> getTeamInSeasons() {
        return this.teamInSeasons;
    }

    public void setTeamInSeasons(Set<TeamInSeason> teamInSeasons) {
        if (this.teamInSeasons != null) {
            this.teamInSeasons.forEach(i -> i.setTeam(null));
        }
        if (teamInSeasons != null) {
            teamInSeasons.forEach(i -> i.setTeam(this));
        }
        this.teamInSeasons = teamInSeasons;
    }

    public Team teamInSeasons(Set<TeamInSeason> teamInSeasons) {
        this.setTeamInSeasons(teamInSeasons);
        return this;
    }

    public Team addTeamInSeason(TeamInSeason teamInSeason) {
        this.teamInSeasons.add(teamInSeason);
        teamInSeason.setTeam(this);
        return this;
    }

    public Team removeTeamInSeason(TeamInSeason teamInSeason) {
        this.teamInSeasons.remove(teamInSeason);
        teamInSeason.setTeam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return getId() != null && getId().equals(((Team) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", teamName='" + getTeamName() + "'" +
            "}";
    }
}
