package org.perfect047.sqllineagevisualizer.infrastructure.database;

import org.perfect047.sqllineagevisualizer.model.*;

import java.sql.*;
import java.util.*;

public class SchemaEnricher {

    public static SchemaMetadata enrich(
            SchemaMetadata raw,
            Connection conn,
            String dbName
    ) throws SQLException {

        DatabaseMetaData meta = conn.getMetaData();
        Map<String, TableInfo> enrichedTables = new HashMap<>();

        for (TableInfo table : raw.getTables().values()) {

            String tableName = table.getTableName();
            Map<String, ColumnInfo> columnMap = new HashMap<>();

            // 🔹 Copy base columns
            for (ColumnInfo col : table.getColumns()) {
                columnMap.put(
                        col.getName(),
                        new ColumnInfo(col.getName(), col.getType(), false, false)
                );
            }

            // 🔑 PRIMARY KEYS
            try (ResultSet rsPk = meta.getPrimaryKeys(dbName, null, tableName)) {
                while (rsPk.next()) {
                    String colName = rsPk.getString("COLUMN_NAME");

                    columnMap.computeIfPresent(colName, (k, old) ->
                            new ColumnInfo(
                                    old.getName(),
                                    old.getType(),
                                    true,
                                    old.isForeignKey()
                            )
                    );
                }
            }

            // 🔗 FOREIGN KEYS
            try (ResultSet rsFk = meta.getImportedKeys(dbName, null, tableName)) {
                while (rsFk.next()) {
                    String colName = rsFk.getString("FKCOLUMN_NAME");

                    columnMap.computeIfPresent(colName, (k, old) ->
                            new ColumnInfo(
                                    old.getName(),
                                    old.getType(),
                                    old.isPrimaryKey(),
                                    true
                            )
                    );
                }
            }

            enrichedTables.put(
                    tableName,
                    new TableInfo(
                            tableName,
                            new ArrayList<>(columnMap.values())
                    )
            );
        }

        return new SchemaMetadata(enrichedTables, raw.getRelationships());
    }
}