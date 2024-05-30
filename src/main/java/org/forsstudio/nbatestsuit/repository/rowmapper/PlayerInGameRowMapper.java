package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PlayerInGame}, with proper type conversions.
 */
@Service
public class PlayerInGameRowMapper implements BiFunction<Row, String, PlayerInGame> {

    private final ColumnConverter converter;

    public PlayerInGameRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PlayerInGame} stored in the database.
     */
    @Override
    public PlayerInGame apply(Row row, String prefix) {
        PlayerInGame entity = new PlayerInGame();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPoints(converter.fromRow(row, prefix + "_points", Integer.class));
        entity.setRebounds(converter.fromRow(row, prefix + "_rebounds", Integer.class));
        entity.setAssists(converter.fromRow(row, prefix + "_assists", Integer.class));
        entity.setSteals(converter.fromRow(row, prefix + "_steals", Integer.class));
        entity.setBlocks(converter.fromRow(row, prefix + "_blocks", Integer.class));
        entity.setFouls(converter.fromRow(row, prefix + "_fouls", Integer.class));
        entity.setTurnovers(converter.fromRow(row, prefix + "_turnovers", Integer.class));
        entity.setPlayed(converter.fromRow(row, prefix + "_played", Float.class));
        entity.setTeamId(converter.fromRow(row, prefix + "_team_id", Long.class));
        entity.setPlayerId(converter.fromRow(row, prefix + "_player_id", Long.class));
        entity.setGameId(converter.fromRow(row, prefix + "_game_id", Long.class));
        return entity;
    }
}
