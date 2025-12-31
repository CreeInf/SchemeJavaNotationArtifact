package ch.sjna.model;

import java.util.*;

public class SchemaDefinition implements Node {
    private final String name;
    private final Map<String, SchemaProperty> properties = new LinkedHashMap<>();
    private String comment;

    public SchemaDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addProperty(SchemaProperty property) {
        properties.put(property.getKey(), property);
    }

    public SchemaProperty getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, SchemaProperty> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public NodeType getType() {
        return NodeType.SCHEMA;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}

