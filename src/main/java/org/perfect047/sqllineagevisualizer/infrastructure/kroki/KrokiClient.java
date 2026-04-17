package org.perfect047.sqllineagevisualizer.infrastructure.kroki;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class KrokiClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static String render(String diagram, String type) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(diagram, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kroki.io/" + type + "/svg",
                HttpMethod.POST,
                request,
                String.class
        );

        String body = response.getBody();

        if (body == null || body.startsWith("<!DOCTYPE html>")) {
            throw new RuntimeException("Kroki failed for " + type);
        }

        return body;
    }
}