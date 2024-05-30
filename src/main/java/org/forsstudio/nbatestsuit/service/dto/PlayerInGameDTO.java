package org.forsstudio.nbatestsuit.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.forsstudio.nbatestsuit.domain.PlayerInGame} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerInGameDTO implements Serializable {

    private Long id;

    private Integer points;

    private Integer rebounds;

    private Integer assists;

    private Integer steals;

    private Integer blocks;

    private Integer fouls;

    private Integer turnovers;

    private Float played;

    private TeamInGameDTO team;

    private PlayerDTO player;

    private GameDTO game;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getRebounds() {
        return rebounds;
    }

    public void setRebounds(Integer rebounds) {
        this.rebounds = rebounds;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getSteals() {
        return steals;
    }

    public void setSteals(Integer steals) {
        this.steals = steals;
    }

    public Integer getBlocks() {
        return blocks;
    }

    public void setBlocks(Integer blocks) {
        this.blocks = blocks;
    }

    public Integer getFouls() {
        return fouls;
    }

    public void setFouls(Integer fouls) {
        this.fouls = fouls;
    }

    public Integer getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(Integer turnovers) {
        this.turnovers = turnovers;
    }

    public Float getPlayed() {
        return played;
    }

    public void setPlayed(Float played) {
        this.played = played;
    }

    public TeamInGameDTO getTeam() {
        return team;
    }

    public void setTeam(TeamInGameDTO team) {
        this.team = team;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
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
        if (!(o instanceof PlayerInGameDTO)) {
            return false;
        }

        PlayerInGameDTO playerInGameDTO = (PlayerInGameDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, playerInGameDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerInGameDTO{" +
            "id=" + getId() +
            ", points=" + getPoints() +
            ", rebounds=" + getRebounds() +
            ", assists=" + getAssists() +
            ", steals=" + getSteals() +
            ", blocks=" + getBlocks() +
            ", fouls=" + getFouls() +
            ", turnovers=" + getTurnovers() +
            ", played=" + getPlayed() +
            ", team=" + getTeam() +
            ", player=" + getPlayer() +
            ", game=" + getGame() +
            "}";
    }
}
