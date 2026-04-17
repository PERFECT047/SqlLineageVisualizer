package org.perfect047.sqllineagevisualizer.service;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.port.SchemaExtractor;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.strategy.VisualizerStrategyFactory;
import org.perfect047.sqllineagevisualizer.web.dto.DbRequest;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisualizerService {
    private final SchemaExtractor extractor;
    private final VisualizerStrategyFactory strategyFactory;

    public String generateSvg(DbRequest request) throws Exception {
        SchemaMetadata metadata = extractor.extract(
                request.getUrl(),
                request.getUsername(),
                request.getPassword()
        );

        VisualizerStrategy strategy =
                strategyFactory.getStrategy(request.getVisualizerType());

        return strategy.generateImageUrl(metadata);
    }
}