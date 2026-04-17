package org.perfect047.sqllineagevisualizer.controller;

import lombok.RequiredArgsConstructor;
import org.perfect047.sqllineagevisualizer.service.VisualizerService;
import org.perfect047.sqllineagevisualizer.web.dto.DbRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VisualizerApiController {

    private final VisualizerService visualizerService;

    @PostMapping(value = "/api/visualize", produces = "image/svg+xml")
    public String visualize(@RequestBody DbRequest request) {
        try {
            return visualizerService.generateSvg(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}