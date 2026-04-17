package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.infrastructure.utils.Base64Encoder;
import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;

import org.springframework.stereotype.Component;

@Component
public class KrokiMermaidStrategy implements VisualizerStrategy {
    @Override
    public VisualizerType getType() { return VisualizerType.MERMAID; }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {
        StringBuilder sb = new StringBuilder("erDiagram\n");
        metadata.getTables().forEach((name, table) -> {
            sb.append("    ").append(name).append(" {\n");
            table.getColumns().forEach(c -> sb.append("        ").append(c.getType()).append(" ").append(c.getName()).append("\n"));
            sb.append("    }\n");
        });
        metadata.getRelationships().forEach(r ->
                sb.append(String.format("    %s ||--o{ %s : \"references\"\n", r.getFromTable(), r.getToTable())));

        return "https://kroki.io/mermaid/svg/" + Base64Encoder.encode(sb.toString());
    }
}
