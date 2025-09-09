package io.samancore.template_builder.model;

import java.util.Map;

public record NodeDetail(String name, Map<String, Object> properties, String lastModified) {
}
