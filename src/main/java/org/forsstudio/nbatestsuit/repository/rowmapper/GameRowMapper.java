package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.Game;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Game}, with proper type conversions.
 */
@Service
public class GameRowMapper implements BiFunction<Row, String, Game> {

    private final ColumnConverter converter;

    public GameRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Game} stored in the database.
     */
    @Override
    public Game apply(Row row, String prefix) {
        Game entity = new Game();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setSeasonId(converter.fromRow(row, prefix + "_season_id", Long.class));
        return entity;
    }
}
