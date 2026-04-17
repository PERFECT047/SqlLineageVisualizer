package org.perfect047.sqllineagevisualizer.model;

import lombok.Value;

@Value
public class ColumnInfo {
    String name;
    String type;

    boolean primaryKey;
    boolean foreignKey;

    public ColumnInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.primaryKey = false;
        this.foreignKey = false;
    }

    public ColumnInfo(String name, String type, boolean primaryKey, boolean foreignKey) {
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
    }
}