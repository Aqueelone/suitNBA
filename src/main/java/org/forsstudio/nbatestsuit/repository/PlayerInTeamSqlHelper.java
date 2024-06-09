package org.forsstudio.nbatestsuit.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PlayerInTeamSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));

        columns.add(Column.aliased("player_id", table, columnPrefix + "_player_id"));
        columns.add(Column.aliased("team_in_season_id", table, columnPrefix + "_team_in_season_id"));
        return columns;
    }
}
