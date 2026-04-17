package org.perfect047.sqllineagevisualizer.port;

import org.perfect047.sqllineagevisualizer.model.SchemaMetadata;

public interface SchemaExtractor {
    SchemaMetadata extract(String url, String user, String pass) throws Exception;
}
