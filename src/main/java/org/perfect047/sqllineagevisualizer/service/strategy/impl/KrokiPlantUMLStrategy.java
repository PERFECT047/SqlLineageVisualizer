package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.infrastructure.utils.Base64Encoder;
import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;

import org.springframework.stereotype.Component;

@Component
public class KrokiPlantUMLStrategy implements VisualizerStrategy {
    @Override
    public VisualizerType getType() { return VisualizerType.PLANTUML; }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {
        StringBuilder sb = new StringBuilder("@startuml\nskinparam linetype polyline\n");
        metadata.getTables().forEach((name, table) -> {
            sb.append("entity ").append(name).append(" {\n");
            table.getColumns().forEach(c -> sb.append("  ").append(c.getName()).append(" : ").append(c.getType()).append("\n"));
            sb.append("}\n");
        });
        metadata.getRelationships().forEach(r ->
                sb.append(String.format("%s --o{ %s\n", r.getFromTable(), r.getToTable())));
        sb.append("@enduml");

        return "https://kroki.io/plantuml/svg/" + Base64Encoder.encode(sb.toString());
    }
}
