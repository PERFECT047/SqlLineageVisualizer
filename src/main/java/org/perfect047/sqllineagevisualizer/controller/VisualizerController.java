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

    @PostMapping("/visualize")
    public String visualize(@ModelAttribute DbRequest request, Model model) {
        try {
            String url = visualizerService.getVisualizedUrl(request);
            model.addAttribute("imageUrl", url);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }
}
