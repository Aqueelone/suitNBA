package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PlayerInTeam}, with proper type conversions.
 */
@Service
public class PlayerInTeamRowMapper implements BiFunction<Row, String, PlayerInTeam> {

    private final ColumnConverter converter;

    public PlayerInTeamRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PlayerInTeam} stored in the database.
     */
    @Override
    public PlayerInTeam apply(Row row, String prefix) {
        PlayerInTeam entity = new PlayerInTeam();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPlayerId(converter.fromRow(row, prefix + "_player_id", Long.class));
        entity.setTeamInSeasonId(converter.fromRow(row, prefix + "_team_in_season_id", Long.class));
        return entity;
    }
}
