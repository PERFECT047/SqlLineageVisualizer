package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KrokiMermaidStrategy implements VisualizerStrategy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VisualizerType getType() {
        return VisualizerType.MERMAID;
    }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {

        StringBuilder sb = new StringBuilder("erDiagram\n\n");

        // 🔥 TABLES
        metadata.getTables().forEach((name, table) -> {

            // 🚨 Skip problematic system tables
            if (isSystemTable(name)) return;

            String tableName = sanitizeName(name);

            sb.append("    ").append(tableName).append(" {\n");

            table.getColumns().forEach(c -> {
                sb.append("        ")
                        .append(normalizeType(c.getType()))
                        .append(" ")
                        .append(sanitizeName(c.getName()))
                        .append("\n");
            });

            sb.append("    }\n\n"); // important spacing
        });

        // 🔥 RELATIONSHIPS
        metadata.getRelationships().forEach(r -> {
            String from = sanitizeName(r.getFromTable());
            String to = sanitizeName(r.getToTable());

            if (isSystemTable(from) || isSystemTable(to)) return;

            sb.append(String.format(
                    "    %s ||--o{ %s : references\n",
                    from,
                    to
            ));
        });

        // 🔥 CALL KROKI (POST)
        String krokiUrl = "https://kroki.io/mermaid/svg";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                krokiUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Kroki Mermaid failed: " + response.getStatusCode());
        }

        return response.getBody();
    }

    // 🔧 HELPERS (ALL FIXES LIVE HERE)

    private String normalizeType(String type) {
        if (type == null) return "string";

        type = type.toUpperCase();

        if (type.contains("INT")) return "int";
        if (type.contains("CHAR") || type.contains("TEXT")) return "string";
        if (type.contains("DATE") || type.contains("TIME")) return "datetime";
        if (type.contains("BOOL")) return "boolean";

        return "string"; // fallback
    }

    private String sanitizeName(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private boolean isSystemTable(String name) {
        if (name == null) return true;

        String lower = name.toLowerCase();

        return lower.startsWith("mysql")
                || lower.startsWith("sys")
                || lower.startsWith("performance_schema")
                || lower.equals("user"); // common troublemaker
    }
}