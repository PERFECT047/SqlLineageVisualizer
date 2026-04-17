package org.perfect047.sqllineagevisualizer.service.strategy;

import lombok.RequiredArgsConstructor;
import org.perfect047.sqllineagevisualizer.port.VisualizerStrategy;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class VisualizerStrategyFactory {
    private final List<VisualizerStrategy> strategyList;

    public VisualizerStrategy getStrategy(VisualizerType type) {
        return strategyList.stream()
                .filter(s -> s.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported strategy: " + type));
    }
}
