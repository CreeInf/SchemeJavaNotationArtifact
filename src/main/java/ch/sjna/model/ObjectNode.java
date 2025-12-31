package ch.sjna.model;

import java.util.*;

public class ObjectNode implements Node {
    private final Map<String, PropertyNode> properties = new LinkedHashMap<>();
    private String comment;

    public void addProperty(PropertyNode property) {
        properties.put(property.getKey(), property);
    }

    public PropertyNode getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, PropertyNode> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public NodeType getType() {
        return NodeType.OBJECT;
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
