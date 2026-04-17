package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.infrastructure.kroki.KrokiClient;
import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;
import org.springframework.stereotype.Component;

@Component
public class KrokiMermaidStrategy implements VisualizerStrategy {

    @Override
    public VisualizerType getType() {
        return VisualizerType.MERMAID;
    }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {

        StringBuilder sb = new StringBuilder("erDiagram\n");

        metadata.getTables().forEach((name, table) -> {

            sb.append("    ").append(sanitize(name)).append(" {\n");

            table.getColumns().forEach(c -> {

                sb.append("        ")
                        .append(mapType(c.getType()))
                        .append(" ")
                        .append(sanitize(c.getName()));

                if (c.isPrimaryKey()) sb.append(" PK");
                if (c.isForeignKey()) sb.append(" FK");

                sb.append("\n");
            });

            sb.append("    }\n");
        });

        metadata.getRelationships().forEach(r ->
                sb.append("    ")
                        .append(sanitize(r.getFromTable()))
                        .append(" ||--o{ ")
                        .append(sanitize(r.getToTable()))
                        .append(" : \"ref\"\n")
        );

        return KrokiClient.render(sb.toString(), "mermaid");
    }

    private String mapType(String type) {
        if (type == null) return "string";

        type = type.toLowerCase();

        if (type.contains("int")) return "int";
        if (type.contains("char") || type.contains("text")) return "string";
        if (type.contains("date") || type.contains("time")) return "datetime";
        if (type.contains("bool")) return "boolean";

        return "string";
    }

    private String sanitize(String input) {
        if (input == null) return "unknown";
        return input.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}