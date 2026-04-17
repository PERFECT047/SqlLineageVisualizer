package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KrokiGraphvizStrategy implements VisualizerStrategy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VisualizerType getType() {
        return VisualizerType.GRAPHVIZ;
    }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {

        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("node [fontname=\"DejaVu Sans\"];\n");
        sb.append("rankdir=TB;\n");
        sb.append("nodesep=0.4;\n");
        sb.append("ranksep=0.6;\n");
        sb.append("splines=ortho;\n");  // cleaner edges
        sb.append("concentrate=true;\n"); // merge edges
        sb.append("node [margin=0.05];\n");
        sb.append("ratio=compress;\n");
        sb.append("node [shape=plaintext];\n");
        sb.append("edge [color=\"#f87171\"];\n");

        metadata.getTables().forEach((name, table) -> {

            String safeName = sanitize(name);

            sb.append(safeName).append(" [label=<\n");
            sb.append("<TABLE BORDER=\"1\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");

// 🔷 Table name
            sb.append("<TR><TD BGCOLOR=\"#60a5fa\"><B>")
                    .append(safeName)
                    .append("</B></TD></TR>\n");

// 🔷 Columns
            table.getColumns().forEach(c -> {

                String col = sanitize(c.getName());

                String color = "#4d4d4d";
                String prefix = "";

                if (c.isPrimaryKey()) {
                    prefix = "[PK] ";
                    color = "#facc15"; // yellow
                } else if (c.isForeignKey()) {
                    prefix = "[FK] ";
                    color = "#f87171"; // red
                }

                sb.append("<TR><TD ALIGN=\"LEFT\"><FONT COLOR=\"")
                        .append(color)
                        .append("\">")
                        .append(prefix)
                        .append(col)
                        .append("</FONT></TD></TR>\n");
            });

            sb.append("</TABLE>\n>];\n");
        });

        metadata.getRelationships().forEach(r -> {
            String from = sanitize(r.getFromTable());
            String to = sanitize(r.getToTable());

            sb.append(from)
                    .append(" -> ")
                    .append(to)
                    .append(";\n");
        });

        sb.append("}");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kroki.io/graphviz/svg",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Graphviz failed: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private String sanitize(String input) {
        if (input == null) return "unknown";
        return input.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}