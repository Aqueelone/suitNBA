package org.forsstudio.nbatestsuit.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.forsstudio.nbatestsuit.domain.Season;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Season}, with proper type conversions.
 */
@Service
public class SeasonRowMapper implements BiFunction<Row, String, Season> {

    private final ColumnConverter converter;

    public SeasonRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Season} stored in the database.
     */
    @Override
    public Season apply(Row row, String prefix) {
        Season entity = new Season();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setSeasonName(converter.fromRow(row, prefix + "_season_name", String.class));
        return entity;
    }
}
