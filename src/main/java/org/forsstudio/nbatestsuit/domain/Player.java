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
 * A Player.
 */
@Table("player")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "player")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Transient
    @JsonIgnoreProperties(value = { "team", "player", "game" }, allowSetters = true)
    private Set<PlayerInGame> playerInGames = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "player", "teamInSeason" }, allowSetters = true)
    private Set<PlayerInTeam> playerInTeams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Player id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Player name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PlayerInGame> getPlayerInGames() {
        return this.playerInGames;
    }

    public void setPlayerInGames(Set<PlayerInGame> playerInGames) {
        if (this.playerInGames != null) {
            this.playerInGames.forEach(i -> i.setPlayer(null));
        }
        if (playerInGames != null) {
            playerInGames.forEach(i -> i.setPlayer(this));
        }
        this.playerInGames = playerInGames;
    }

    public Player playerInGames(Set<PlayerInGame> playerInGames) {
        this.setPlayerInGames(playerInGames);
        return this;
    }

    public Player addPlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.add(playerInGame);
        playerInGame.setPlayer(this);
        return this;
    }

    public Player removePlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.remove(playerInGame);
        playerInGame.setPlayer(null);
        return this;
    }

    public Set<PlayerInTeam> getPlayerInTeams() {
        return this.playerInTeams;
    }

    public void setPlayerInTeams(Set<PlayerInTeam> playerInTeams) {
        if (this.playerInTeams != null) {
            this.playerInTeams.forEach(i -> i.setPlayer(null));
        }
        if (playerInTeams != null) {
            playerInTeams.forEach(i -> i.setPlayer(this));
        }
        this.playerInTeams = playerInTeams;
    }

    public Player playerInTeams(Set<PlayerInTeam> playerInTeams) {
        this.setPlayerInTeams(playerInTeams);
        return this;
    }

    public Player addPlayerInTeam(PlayerInTeam playerInTeam) {
        this.playerInTeams.add(playerInTeam);
        playerInTeam.setPlayer(this);
        return this;
    }

    public Player removePlayerInTeam(PlayerInTeam playerInTeam) {
        this.playerInTeams.remove(playerInTeam);
        playerInTeam.setPlayer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        return getId() != null && getId().equals(((Player) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
