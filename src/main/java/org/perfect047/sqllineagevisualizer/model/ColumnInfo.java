package org.perfect047.sqllineagevisualizer.model;

import lombok.Value;

@Value // Immutable
public class ColumnInfo {
    String name;
    String type;
}