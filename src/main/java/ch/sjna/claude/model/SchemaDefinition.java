package ch.sjna.claude.model;

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

    public ObjectNode createInstance() {
        return createInstance(new HashMap<>());
    }

    public ObjectNode createInstance(Map<String, Object> values) {
        ObjectNode obj = new ObjectNode();

        for (SchemaProperty schemaProp : properties.values()) {
            String key = schemaProp.getKey();
            Object value = values.get(key);

            ValueNode valueNode;
            if (value != null) {
                valueNode = convertToValueNode(value, schemaProp);
            } else {
                // Default-Wert wenn nicht angegeben
                if (schemaProp.hasEnum()) {
                    // Erste Option als Default
                    String defaultValue = schemaProp.getEnumDefinition().getOptions().get(0);
                    valueNode = new ValueNode(defaultValue, ValueNode.ValueType.IDENTIFIER);
                } else {
                    // Leerer String als Default
                    valueNode = new ValueNode("", ValueNode.ValueType.STRING);
                }
            }

            PropertyNode prop = new PropertyNode(
                    key,
                    valueNode,
                    schemaProp.getEnumDefinition());

            if (schemaProp.getDescription() != null) {
                prop.setComment(schemaProp.getDescription());
            }

            obj.addProperty(prop);
        }

        return obj;
    }

    private ValueNode convertToValueNode(Object value, SchemaProperty schemaProp) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (schemaProp.hasEnum()) {
                if (!schemaProp.getEnumDefinition().isValid(strValue)) {
                    throw new IllegalArgumentException(
                            "Invalid enum value '" + strValue + "' for property '" +
                                    schemaProp.getKey() + "'. Allowed: " +
                                    schemaProp.getEnumDefinition().getOptions());
                }
                return new ValueNode(strValue, ValueNode.ValueType.IDENTIFIER);
            }
            return new ValueNode(strValue, ValueNode.ValueType.STRING);
        } else if (value instanceof Number) {
            return new ValueNode(value, ValueNode.ValueType.NUMBER);
        } else if (value instanceof Boolean) {
            return new ValueNode(value, ValueNode.ValueType.BOOLEAN);
        } else if (value instanceof ObjectNode) {
            return new ValueNode(value, ValueNode.ValueType.OBJECT);
        } else if (value instanceof Map) {
            // Map zu ObjectNode konvertieren
            ObjectNode nested = new ObjectNode();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                ValueNode nestedValue = convertToValueNode(entry.getValue(), schemaProp);
                PropertyNode nestedProp = new PropertyNode(entry.getKey(), nestedValue, null);
                nested.addProperty(nestedProp);
            }
            return new ValueNode(nested, ValueNode.ValueType.OBJECT);
        } else {
            return new ValueNode(value.toString(), ValueNode.ValueType.STRING);
        }
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
