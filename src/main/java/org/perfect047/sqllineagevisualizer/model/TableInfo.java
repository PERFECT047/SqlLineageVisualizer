package org.perfect047.sqllineagevisualizer.model;

import lombok.Value;
import java.util.List;

@Value
public class TableInfo {
    String tableName;
    List<ColumnInfo> columns;
}
