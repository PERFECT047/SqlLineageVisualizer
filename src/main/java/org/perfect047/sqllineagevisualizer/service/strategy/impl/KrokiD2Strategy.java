package org.perfect047.sqllineagevisualizer.service.strategy.impl;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KrokiD2Strategy implements VisualizerStrategy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VisualizerType getType() {
        return VisualizerType.D2;
    }

    @Override
    public String generateImageUrl(SchemaMetadata metadata) {

        StringBuilder sb = new StringBuilder();

        metadata.getTables().forEach((name, table) -> {
            sb.append(name).append(": {\n");

            table.getColumns().forEach(c ->
                    sb.append("  ").append(c.getName()).append("\n")
            );

            sb.append("}\n\n");
        });

        metadata.getRelationships().forEach(r ->
                sb.append(r.getFromTable())
                        .append(" -> ")
                        .append(r.getToTable())
                        .append("\n")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kroki.io/d2/svg",
                HttpMethod.POST,
                request,
                String.class
        );

        return response.getBody();
    }
}
