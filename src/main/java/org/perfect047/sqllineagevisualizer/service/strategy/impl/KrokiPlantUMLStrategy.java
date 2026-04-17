package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.infrastructure.kroki.KrokiClient;
import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KrokiPlantUMLStrategy implements VisualizerStrategy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VisualizerType getType() {
        return VisualizerType.PLANTUML;
    }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {

        StringBuilder sb = new StringBuilder("@startuml\n");

        metadata.getTables().forEach((name, table) -> {
            sb.append("entity ").append(name).append(" {\n");

            table.getColumns().forEach(c -> {
                String prefix = "";

                if (c.isPrimaryKey()) prefix = "* ";
                else if (c.isForeignKey()) prefix = "+ ";

                sb.append("  ")
                        .append(prefix)
                        .append(c.getName())
                        .append(" : ")
                        .append(c.getType())
                        .append("\n");
            });

            sb.append("}\n");
        });

        metadata.getRelationships().forEach(r ->
                sb.append(r.getFromTable())
                        .append(" ||--o{ ")
                        .append(r.getToTable())
                        .append("\n")
        );

        sb.append("@enduml");

        return KrokiClient.render(sb.toString(), "plantuml");
    }
}