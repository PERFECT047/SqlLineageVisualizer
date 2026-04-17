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

        sb.append("rankdir=LR;\n");
        sb.append("splines=ortho;\n");
        sb.append("nodesep=0.6;\n");
        sb.append("ranksep=1.2;\n");

        sb.append("node [shape=record, style=filled, fillcolor=\"#E3F2FD\", fontname=\"Helvetica\"];\n");
        sb.append("edge [color=\"#555555\"];\n");

        metadata.getTables().forEach((name, table) -> {

            String safeName = sanitize(name);

            sb.append(safeName).append(" [label=\"{")
                    .append(safeName).append("|");

            table.getColumns().forEach(c -> {
                sb.append(sanitize(c.getName())).append("\\l");
            });

            sb.append("}\"];\n");
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