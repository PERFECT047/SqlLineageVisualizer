package org.perfect047.sqllineagevisualizer.model;

import lombok.Value;
import java.util.List;
import java.util.Map;

@Value
public class SchemaMetadata {
    Map<String, TableInfo> tables;
    List<Relationship> relationships;

    @Value
    public static class Relationship {
        String fromTable;
        String toTable;
    }
}
