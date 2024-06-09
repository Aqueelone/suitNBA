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
 * A TeamInGame.
 */
@Table("team_in_game")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "teamingame")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamInGame implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "team", "player", "game" }, allowSetters = true)
    private Set<PlayerInGame> playerInGames = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "teamInGames", "team", "season", "playerInTeams" }, allowSetters = true)
    private TeamInSeason team;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "teamInGames", "season" }, allowSetters = true)
    private Game game;

    @Column("team_id")
    private Long teamId;

    @Column("game_id")
    private Long gameId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamInGame id(Long id) {
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
            this.playerInGames.forEach(i -> i.setTeam(null));
        }
        if (playerInGames != null) {
            playerInGames.forEach(i -> i.setTeam(this));
        }
        this.playerInGames = playerInGames;
    }

    public TeamInGame playerInGames(Set<PlayerInGame> playerInGames) {
        this.setPlayerInGames(playerInGames);
        return this;
    }

    public TeamInGame addPlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.add(playerInGame);
        playerInGame.setTeam(this);
        return this;
    }

    public TeamInGame removePlayerInGame(PlayerInGame playerInGame) {
        this.playerInGames.remove(playerInGame);
        playerInGame.setTeam(null);
        return this;
    }

    public TeamInSeason getTeam() {
        return this.team;
    }

    public void setTeam(TeamInSeason teamInSeason) {
        this.team = teamInSeason;
        this.teamId = teamInSeason != null ? teamInSeason.getId() : null;
    }

    public TeamInGame team(TeamInSeason teamInSeason) {
        this.setTeam(teamInSeason);
        return this;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
        this.gameId = game != null ? game.getId() : null;
    }

    public TeamInGame game(Game game) {
        this.setGame(game);
        return this;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(Long teamInSeason) {
        this.teamId = teamInSeason;
    }

    public Long getGameId() {
        return this.gameId;
    }

    public void setGameId(Long game) {
        this.gameId = game;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamInGame)) {
            return false;
        }
        return getId() != null && getId().equals(((TeamInGame) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamInGame{" +
            "id=" + getId() +
            "}";
    }
}
