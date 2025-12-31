package ch.sjna;

import ch.sjna.model.*;
import java.util.*;

public class SJNAConfig {
    private final Document document;

    public SJNAConfig(Document document) {
        this.document = document;
    }

    public String getString(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getValue().asString();
        }
        throw new IllegalArgumentException("Path does not point to a string value: " + path);
    }

    public String getString(String path, String defaultValue) {
        try {
            return getString(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Number getNumber(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getValue().asNumber();
        }
        throw new IllegalArgumentException("Path does not point to a number value: " + path);
    }

    public Number getNumber(String path, Number defaultValue) {
        try {
            return getNumber(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Integer getInt(String path) {
        return getNumber(path).intValue();
    }

    public Integer getInt(String path, Integer defaultValue) {
        try {
            return getInt(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Long getLong(String path) {
        return getNumber(path).longValue();
    }

    public Long getLong(String path, Long defaultValue) {
        try {
            return getLong(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Double getDouble(String path) {
        return getNumber(path).doubleValue();
    }

    public Double getDouble(String path, Double defaultValue) {
        try {
            return getDouble(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Boolean getBoolean(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getValue().asBoolean();
        }
        throw new IllegalArgumentException("Path does not point to a boolean value: " + path);
    }

    public Boolean getBoolean(String path, Boolean defaultValue) {
        try {
            return getBoolean(path);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public ObjectNode getObject(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getValue().asObject();
        }
        throw new IllegalArgumentException("Path does not point to an object: " + path);
    }

    public boolean hasPath(String path) {
        try {
            navigate(path);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Map<String, Object> getAsMap(String path) {
        ObjectNode obj = getObject(path);
        return convertObjectToMap(obj);
    }

    public Map<String, Object> getAllAsMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Node> entry : document.getRoot().entrySet()) {
            if (entry.getValue() instanceof PropertyNode) {
                PropertyNode prop = (PropertyNode) entry.getValue();
                result.put(entry.getKey(), convertValueToObject(prop.getValue()));
            }
        }
        return result;
    }

    private Object convertValueToObject(ValueNode value) {
        switch (value.getValueType()) {
            case STRING:
            case IDENTIFIER:
                return value.asString();
            case NUMBER:
                return value.asNumber();
            case BOOLEAN:
                return value.asBoolean();
            case OBJECT:
                return convertObjectToMap(value.asObject());
            default:
                return value.getValue();
        }
    }

    private Map<String, Object> convertObjectToMap(ObjectNode obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, PropertyNode> entry : obj.getProperties().entrySet()) {
            map.put(entry.getKey(), convertValueToObject(entry.getValue().getValue()));
        }
        return map;
    }

    private Node navigate(String path) {
        String[] parts = path.split("\\.");
        Node current = null;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            if (part.isEmpty()) {
                throw new IllegalArgumentException("Invalid path (empty segment): " + path);
            }

            if (i == 0) {
                // Root-Ebene
                current = document.get(part);
            } else {
                // Verschachtelte Ebenen
                if (current instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) current;
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        ObjectNode obj = prop.getValue().asObject();
                        current = obj.getProperty(part);
                    } else {
                        throw new IllegalArgumentException(
                                "Cannot navigate deeper: '" + parts[i-1] + "' is not an object in path: " + path
                        );
                    }
                } else {
                    throw new IllegalArgumentException("Invalid navigation at: " + part + " in path: " + path);
                }
            }

            if (current == null) {
                throw new IllegalArgumentException("Path not found: " + path + " (missing: " + part + ")");
            }
        }

        return current;
    }

    public List<String> getKeys() {
        return new ArrayList<>(document.getRoot().keySet());
    }

    public List<String> getKeys(String path) {
        if (path == null || path.trim().isEmpty()) {
            return getKeys();
        }

        try {
            ObjectNode obj = getObject(path);
            return new ArrayList<>(obj.getProperties().keySet());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    public List<ObjectNode> getObjects(String path) {
        List<ObjectNode> objects = new ArrayList<>();

        if (path == null || path.trim().isEmpty()) {
            // Root-Ebene
            for (Map.Entry<String, Node> entry : document.getRoot().entrySet()) {
                if (entry.getValue() instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) entry.getValue();
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        objects.add(prop.getValue().asObject());
                    }
                }
            }
        } else {
            // Verschachtelte Ebene
            try {
                ObjectNode parentObj = getObject(path);
                for (PropertyNode prop : parentObj.getProperties().values()) {
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        objects.add(prop.getValue().asObject());
                    }
                }
            } catch (IllegalArgumentException e) {
                // Path nicht gefunden oder kein Objekt
            }
        }

        return objects;
    }

    public List<Map<String, Object>> getObjectsAsMap(String path) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ObjectNode obj : getObjects(path)) {
            result.add(convertObjectToMap(obj));
        }
        return result;
    }

    public Map<String, ObjectNode> getObjectsWithKeys(String path) {
        Map<String, ObjectNode> result = new LinkedHashMap<>();

        if (path == null || path.trim().isEmpty()) {
            // Root-Ebene
            for (Map.Entry<String, Node> entry : document.getRoot().entrySet()) {
                if (entry.getValue() instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) entry.getValue();
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        result.put(entry.getKey(), prop.getValue().asObject());
                    }
                }
            }
        } else {
            // Verschachtelte Ebene
            try {
                ObjectNode parentObj = getObject(path);
                for (Map.Entry<String, PropertyNode> entry : parentObj.getProperties().entrySet()) {
                    if (entry.getValue().getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        result.put(entry.getKey(), entry.getValue().getValue().asObject());
                    }
                }
            } catch (IllegalArgumentException e) {
                // Path nicht gefunden
            }
        }

        return result;
    }

    public Document getDocument() {
        return document;
    }
}