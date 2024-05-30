package org.forsstudio.nbatestsuit.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PlayerInGameSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("points", table, columnPrefix + "_points"));
        columns.add(Column.aliased("rebounds", table, columnPrefix + "_rebounds"));
        columns.add(Column.aliased("assists", table, columnPrefix + "_assists"));
        columns.add(Column.aliased("steals", table, columnPrefix + "_steals"));
        columns.add(Column.aliased("blocks", table, columnPrefix + "_blocks"));
        columns.add(Column.aliased("fouls", table, columnPrefix + "_fouls"));
        columns.add(Column.aliased("turnovers", table, columnPrefix + "_turnovers"));
        columns.add(Column.aliased("played", table, columnPrefix + "_played"));

        columns.add(Column.aliased("team_id", table, columnPrefix + "_team_id"));
        columns.add(Column.aliased("player_id", table, columnPrefix + "_player_id"));
        columns.add(Column.aliased("game_id", table, columnPrefix + "_game_id"));
        return columns;
    }
}
