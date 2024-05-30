package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TeamInSeason}, with proper type conversions.
 */
@Service
public class TeamInSeasonRowMapper implements BiFunction<Row, String, TeamInSeason> {

    private final ColumnConverter converter;

    public TeamInSeasonRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TeamInSeason} stored in the database.
     */
    @Override
    public TeamInSeason apply(Row row, String prefix) {
        TeamInSeason entity = new TeamInSeason();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTeamId(converter.fromRow(row, prefix + "_team_id", Long.class));
        entity.setSeasonId(converter.fromRow(row, prefix + "_season_id", Long.class));
        return entity;
    }
}
