package org.forsstudio.nbatestsuit.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TeamInGameSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));

        columns.add(Column.aliased("team_id", table, columnPrefix + "_team_id"));
        columns.add(Column.aliased("game_id", table, columnPrefix + "_game_id"));
        return columns;
    }
}
