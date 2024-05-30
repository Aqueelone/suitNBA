package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TeamInGame}, with proper type conversions.
 */
@Service
public class TeamInGameRowMapper implements BiFunction<Row, String, TeamInGame> {

    private final ColumnConverter converter;

    public TeamInGameRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TeamInGame} stored in the database.
     */
    @Override
    public TeamInGame apply(Row row, String prefix) {
        TeamInGame entity = new TeamInGame();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTeamId(converter.fromRow(row, prefix + "_team_id", Long.class));
        entity.setGameId(converter.fromRow(row, prefix + "_game_id", Long.class));
        return entity;
    }
}
