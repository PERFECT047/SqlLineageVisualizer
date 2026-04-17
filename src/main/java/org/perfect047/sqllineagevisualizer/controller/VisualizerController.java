package org.perfect047.sqllineagevisualizer.controller;

import lombok.RequiredArgsConstructor;
import org.perfect047.sqllineagevisualizer.service.VisualizerService;
import org.perfect047.sqllineagevisualizer.web.dto.DbRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class VisualizerController {
    private final VisualizerService visualizerService;

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/visualize")
    public String redirectToHome() {
        return "redirect:/";
    }

    @PostMapping("/visualize")
    @ResponseBody
    public String visualize(@RequestBody DbRequest request) {
        try {
            return visualizerService.generateSvg(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
