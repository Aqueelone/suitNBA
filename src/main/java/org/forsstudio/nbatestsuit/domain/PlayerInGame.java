package org.forsstudio.nbatestsuit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PlayerInGame.
 */
@Table("player_in_game")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "playeringame")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerInGame implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("points")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer points;

    @Column("rebounds")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer rebounds;

    @Column("assists")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer assists;

    @Column("steals")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer steals;

    @Column("blocks")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer blocks;

    @Column("fouls")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer fouls;

    @Column("turnovers")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer turnovers;

    @Column("played")
    private Float played;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "team", "game" }, allowSetters = true)
    private TeamInGame team;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "playerInTeams" }, allowSetters = true)
    private Player player;

    @Transient
    @JsonIgnoreProperties(value = { "playerInGames", "teamInGames", "season" }, allowSetters = true)
    private Game game;

    @Column("team_id")
    private Long teamId;

    @Column("player_id")
    private Long playerId;

    @Column("game_id")
    private Long gameId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlayerInGame id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPoints() {
        return this.points;
    }

    public PlayerInGame points(Integer points) {
        this.setPoints(points);
        return this;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getRebounds() {
        return this.rebounds;
    }

    public PlayerInGame rebounds(Integer rebounds) {
        this.setRebounds(rebounds);
        return this;
    }

    public void setRebounds(Integer rebounds) {
        this.rebounds = rebounds;
    }

    public Integer getAssists() {
        return this.assists;
    }

    public PlayerInGame assists(Integer assists) {
        this.setAssists(assists);
        return this;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getSteals() {
        return this.steals;
    }

    public PlayerInGame steals(Integer steals) {
        this.setSteals(steals);
        return this;
    }

    public void setSteals(Integer steals) {
        this.steals = steals;
    }

    public Integer getBlocks() {
        return this.blocks;
    }

    public PlayerInGame blocks(Integer blocks) {
        this.setBlocks(blocks);
        return this;
    }

    public void setBlocks(Integer blocks) {
        this.blocks = blocks;
    }

    public Integer getFouls() {
        return this.fouls;
    }

    public PlayerInGame fouls(Integer fouls) {
        this.setFouls(fouls);
        return this;
    }

    public void setFouls(Integer fouls) {
        this.fouls = fouls;
    }

    public Integer getTurnovers() {
        return this.turnovers;
    }

    public PlayerInGame turnovers(Integer turnovers) {
        this.setTurnovers(turnovers);
        return this;
    }

    public void setTurnovers(Integer turnovers) {
        this.turnovers = turnovers;
    }

    public Float getPlayed() {
        return this.played;
    }

    public PlayerInGame played(Float played) {
        this.setPlayed(played);
        return this;
    }

    public void setPlayed(Float played) {
        this.played = played;
    }

    public TeamInGame getTeam() {
        return this.team;
    }

    public void setTeam(TeamInGame teamInGame) {
        this.team = teamInGame;
        this.teamId = teamInGame != null ? teamInGame.getId() : null;
    }

    public PlayerInGame team(TeamInGame teamInGame) {
        this.setTeam(teamInGame);
        return this;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.playerId = player != null ? player.getId() : null;
    }

    public PlayerInGame player(Player player) {
        this.setPlayer(player);
        return this;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
        this.gameId = game != null ? game.getId() : null;
    }

    public PlayerInGame game(Game game) {
        this.setGame(game);
        return this;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(Long teamInGame) {
        this.teamId = teamInGame;
    }

    public Long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(Long player) {
        this.playerId = player;
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
        if (!(o instanceof PlayerInGame)) {
            return false;
        }
        return getId() != null && getId().equals(((PlayerInGame) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerInGame{" +
            "id=" + getId() +
            ", points=" + getPoints() +
            ", rebounds=" + getRebounds() +
            ", assists=" + getAssists() +
            ", steals=" + getSteals() +
            ", blocks=" + getBlocks() +
            ", fouls=" + getFouls() +
            ", turnovers=" + getTurnovers() +
            ", played=" + getPlayed() +
            "}";
    }
}
