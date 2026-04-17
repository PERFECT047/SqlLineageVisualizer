package org.perfect047.sqllineagevisualizer.infrastructure.utils;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Base64Encoder {
    public static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
