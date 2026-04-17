package org.perfect047.sqllineagevisualizer.web.dto;

import org.perfect047.sqllineagevisualizer.service.VisualizerType;

import lombok.Data;

@Data
public class DbRequest {
    private String url;
    private String username;
    private String password;
    private VisualizerType visualizerType;
}
