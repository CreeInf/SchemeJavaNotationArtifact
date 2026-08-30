package ch.sjna.claude.model;

import java.util.*;

public class Document implements Node {
    private final Map<String, Node> root = new LinkedHashMap<>();
    private final Map<String, SchemaDefinition> schemas = new LinkedHashMap<>();
    private String comment;

    public void addProperty(String key, Node value) {
        root.put(key, value);
    }

    public Node get(String key) {
        return root.get(key);
    }

    public void addSchema(String name, SchemaDefinition schema) {
        schemas.put(name, schema);
    }

    public SchemaDefinition getSchema(String name) {
        return schemas.get(name);
    }

    public Map<String, Node> getRoot() {
        return Collections.unmodifiableMap(root);
    }

    public Map<String, SchemaDefinition> getSchemas() {
        return Collections.unmodifiableMap(schemas);
    }

    @Override
    public NodeType getType() {
        return NodeType.DOCUMENT;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void removeProperty(String key) {
        root.remove(key);
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}