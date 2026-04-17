package org.perfect047.sqllineagevisualizer.infrastructure.database;

import org.perfect047.sqllineagevisualizer.model.ColumnInfo;
import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.model.TableInfo;
import org.perfect047.sqllineagevisualizer.port.SchemaExtractor;

import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.*;

@Component
public class JdbcSchemaExtractor implements SchemaExtractor {

    @Override
    public SchemaMetadata extract(String url, String user, String pass) throws Exception {

        Map<String, TableInfo> tables = new HashMap<>();
        List<SchemaMetadata.Relationship> relationships = new ArrayList<>();

        String dbName = extractDbName(url);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            DatabaseMetaData meta = conn.getMetaData();

            ResultSet rsTables = meta.getTables(dbName, null, "%", new String[]{"TABLE"});

            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");

                List<ColumnInfo> columns = new ArrayList<>();

                ResultSet rsCols = meta.getColumns(dbName, null, tableName, null);

                while (rsCols.next()) {
                    columns.add(new ColumnInfo(
                            rsCols.getString("COLUMN_NAME"),
                            rsCols.getString("TYPE_NAME")
                    ));
                }

                tables.put(tableName, new TableInfo(tableName, columns));
            }

            for (String table : tables.keySet()) {
                ResultSet rsFk = meta.getImportedKeys(dbName, null, table);

                while (rsFk.next()) {
                    relationships.add(new SchemaMetadata.Relationship(
                            rsFk.getString("PKTABLE_NAME"),
                            rsFk.getString("FKTABLE_NAME")
                    ));
                }
            }

            SchemaMetadata raw = new SchemaMetadata(tables, relationships);
            return SchemaEnricher.enrich(raw, conn, dbName);
        }
    }

    private String extractDbName(String url) {
        String[] parts = url.split("/");
        String dbPart = parts[parts.length - 1];

        if (dbPart.contains("?")) {
            dbPart = dbPart.substring(0, dbPart.indexOf("?"));
        }

        return dbPart;
    }
}