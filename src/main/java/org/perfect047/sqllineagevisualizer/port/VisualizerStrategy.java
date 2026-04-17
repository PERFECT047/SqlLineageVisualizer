package org.perfect047.sqllineagevisualizer.port;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;
import org.perfect047.sqllineagevisualizer.service.VisualizerType;

public interface VisualizerStrategy {
    String generateImageUrl(SchemaMetadata metadata);
    VisualizerType getType();
}
