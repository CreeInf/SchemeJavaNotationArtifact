package ch.sjna;

import ch.sjna.model.*;
import java.util.*;

public class SJNAConfig {
    private final Document document;

    public SJNAConfig(Document document) {
        this.document = document;
    }

    // ========== GET METHODS ==========

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

    public List<String> getList(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            PropertyNode prop = (PropertyNode) node;
            if (prop.getValue().getValueType() == ValueNode.ValueType.LIST) {
                List<ValueNode> list = prop.getValue().asList();
                List<String> result = new ArrayList<>();
                for (ValueNode item : list) {
                    result.add(item.asString());
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Path does not point to a list: " + path);
    }

    public List<Number> getNumberList(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            PropertyNode prop = (PropertyNode) node;
            if (prop.getValue().getValueType() == ValueNode.ValueType.LIST) {
                List<ValueNode> list = prop.getValue().asList();
                List<Number> result = new ArrayList<>();
                for (ValueNode item : list) {
                    result.add(item.asNumber());
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Path does not point to a number list: " + path);
    }

    public List<Integer> getIntList(String path) {
        List<Number> numbers = getNumberList(path);
        List<Integer> result = new ArrayList<>();
        for (Number n : numbers) {
            result.add(n.intValue());
        }
        return result;
    }

    public List<Map<String, Object>> getObjectList(String path) {
        Node node = navigate(path);
        if (node instanceof PropertyNode) {
            PropertyNode prop = (PropertyNode) node;
            if (prop.getValue().getValueType() == ValueNode.ValueType.LIST) {
                List<ValueNode> list = prop.getValue().asList();
                List<Map<String, Object>> result = new ArrayList<>();
                for (ValueNode item : list) {
                    if (item.getValueType() == ValueNode.ValueType.OBJECT) {
                        result.add(convertObjectToMap(item.asObject()));
                    }
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Path does not point to an object list: " + path);
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
            for (Map.Entry<String, Node> entry : document.getRoot().entrySet()) {
                if (entry.getValue() instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) entry.getValue();
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        objects.add(prop.getValue().asObject());
                    }
                }
            }
        } else {
            try {
                ObjectNode parentObj = getObject(path);
                for (PropertyNode prop : parentObj.getProperties().values()) {
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        objects.add(prop.getValue().asObject());
                    }
                }
            } catch (IllegalArgumentException e) {
                // Path nicht gefunden
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
            for (Map.Entry<String, Node> entry : document.getRoot().entrySet()) {
                if (entry.getValue() instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) entry.getValue();
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        result.put(entry.getKey(), prop.getValue().asObject());
                    }
                }
            }
        } else {
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

    // ========== SET METHODS ==========

    public void setString(String path, String value) {
        setValue(path, new ValueNode(value, ValueNode.ValueType.STRING), null);
    }

    public void setInt(String path, int value) {
        setValue(path, new ValueNode(value, ValueNode.ValueType.NUMBER), null);
    }

    public void setLong(String path, long value) {
        setValue(path, new ValueNode(value, ValueNode.ValueType.NUMBER), null);
    }

    public void setDouble(String path, double value) {
        setValue(path, new ValueNode(value, ValueNode.ValueType.NUMBER), null);
    }

    public void setBoolean(String path, boolean value) {
        setValue(path, new ValueNode(value, ValueNode.ValueType.BOOLEAN), null);
    }

    public void setEnum(String path, List<String> options, String value) {
        if (!options.contains(value)) {
            throw new IllegalArgumentException("Value '" + value + "' is not in enum options: " + options);
        }
        EnumDefinition enumDef = new EnumDefinition(options);
        setValue(path, new ValueNode(value, ValueNode.ValueType.IDENTIFIER), enumDef);
    }

    public void setList(String path, List<?> items) {
        List<ValueNode> valueNodes = new ArrayList<>();
        for (Object item : items) {
            valueNodes.add(convertObjectToValueNode(item));
        }
        setValue(path, new ValueNode(valueNodes, ValueNode.ValueType.LIST), null);
    }

    public void setObject(String path, Map<String, Object> values) {
        ObjectNode obj = convertMapToObject(values);
        setValue(path, new ValueNode(obj, ValueNode.ValueType.OBJECT), null);
    }

    // Setzt einen Wert – erstellt ihn wenn er nicht existiert, überschreibt ihn wenn er existiert
    private void setValue(String path, ValueNode valueNode, EnumDefinition enumDef) {
        String[] parts = path.split("\\.");

        if (parts.length == 1) {
            // Root-Ebene
            PropertyNode existing = getExistingProperty(path);
            if (existing != null) {
                // Überschreiben – neuen PropertyNode erstellen
                document.addProperty(parts[0], new PropertyNode(parts[0], valueNode, enumDef != null ? enumDef : existing.getEnumDefinition()));
            } else {
                // Neu erstellen
                document.addProperty(parts[0], new PropertyNode(parts[0], valueNode, enumDef));
            }
        } else {
            // Verschachtelt – zum Parent navigieren
            String parentPath = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
            String lastKey = parts[parts.length - 1];
            ObjectNode parent = getObject(parentPath);
            PropertyNode existing = parent.getProperty(lastKey);
            if (existing != null) {
                parent.addProperty(new PropertyNode(lastKey, valueNode, enumDef != null ? enumDef : existing.getEnumDefinition()));
            } else {
                parent.addProperty(new PropertyNode(lastKey, valueNode, enumDef));
            }
        }
    }

    private PropertyNode getExistingProperty(String key) {
        Node node = document.get(key);
        if (node instanceof PropertyNode) {
            return (PropertyNode) node;
        }
        return null;
    }

    // ========== REMOVE METHODS ==========

    public void remove(String path) {
        String[] parts = path.split("\\.");

        if (parts.length == 1) {
            document.removeProperty(parts[0]);
        } else {
            String parentPath = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
            String lastKey = parts[parts.length - 1];
            ObjectNode parent = getObject(parentPath);
            parent.removeProperty(lastKey);
        }
    }

    // ========== ADD METHODS ==========

    public void addObject(String key, Map<String, Object> values) {
        ObjectNode obj = convertMapToObject(values);
        PropertyNode prop = new PropertyNode(key, new ValueNode(obj, ValueNode.ValueType.OBJECT), null);
        document.addProperty(key, prop);
    }

    public void addObjectAt(String parentPath, String key, Map<String, Object> values) {
        ObjectNode parent = getObject(parentPath);
        ObjectNode obj = convertMapToObject(values);
        PropertyNode prop = new PropertyNode(key, new ValueNode(obj, ValueNode.ValueType.OBJECT), null);
        parent.addProperty(prop);
    }

    public void addFromSchema(String schemaName, String key, Map<String, Object> values) {
        SchemaDefinition schema = document.getSchema(schemaName);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + schemaName);
        }
        ObjectNode obj = schema.createInstance(values);
        PropertyNode prop = new PropertyNode(key, new ValueNode(obj, ValueNode.ValueType.OBJECT), null);
        document.addProperty(key, prop);
    }

    // ========== PRIVATE HELPERS ==========

    private ObjectNode convertMapToObject(Map<String, Object> map) {
        ObjectNode obj = new ObjectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ValueNode valueNode = convertObjectToValueNode(entry.getValue());
            PropertyNode prop = new PropertyNode(entry.getKey(), valueNode, null);
            obj.addProperty(prop);
        }
        return obj;
    }

    private ValueNode convertObjectToValueNode(Object value) {
        if (value instanceof String) {
            return new ValueNode(value, ValueNode.ValueType.STRING);
        } else if (value instanceof Number) {
            return new ValueNode(value, ValueNode.ValueType.NUMBER);
        } else if (value instanceof Boolean) {
            return new ValueNode(value, ValueNode.ValueType.BOOLEAN);
        } else if (value instanceof ObjectNode) {
            return new ValueNode(value, ValueNode.ValueType.OBJECT);
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            ObjectNode nested = convertMapToObject((Map<String, Object>) value);
            return new ValueNode(nested, ValueNode.ValueType.OBJECT);
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            List<ValueNode> items = new ArrayList<>();
            for (Object item : list) {
                items.add(convertObjectToValueNode(item));
            }
            return new ValueNode(items, ValueNode.ValueType.LIST);
        } else {
            return new ValueNode(value.toString(), ValueNode.ValueType.STRING);
        }
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
            case LIST:
                List<ValueNode> list = value.asList();
                List<Object> result = new ArrayList<>();
                for (ValueNode item : list) {
                    result.add(convertValueToObject(item));
                }
                return result;
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
                current = document.get(part);
            } else {
                if (current instanceof PropertyNode) {
                    PropertyNode prop = (PropertyNode) current;
                    if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
                        ObjectNode obj = prop.getValue().asObject();
                        current = obj.getProperty(part);
                    } else {
                        throw new IllegalArgumentException(
                                "Cannot navigate deeper: '" + parts[i-1] + "' is not an object in path: " + path);
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

    // ========== BUILDER CLASSES ==========

    public static class ObjectBuilder {
        private final SJNAConfig config;
        private final String key;
        private final String parentPath;
        private final Map<String, Object> values = new LinkedHashMap<>();

        ObjectBuilder(SJNAConfig config, String key, String parentPath) {
            this.config = config;
            this.key = key;
            this.parentPath = parentPath;
        }

        public ObjectBuilder with(String fieldKey, Object value) {
            values.put(fieldKey, value);
            return this;
        }

        public ObjectBuilder withString(String fieldKey, String value) {
            values.put(fieldKey, value);
            return this;
        }

        public ObjectBuilder withNumber(String fieldKey, Number value) {
            values.put(fieldKey, value);
            return this;
        }

        public ObjectBuilder withInt(String fieldKey, int value) {
            values.put(fieldKey, value);
            return this;
        }

        public ObjectBuilder withBoolean(String fieldKey, boolean value) {
            values.put(fieldKey, value);
            return this;
        }

        public ObjectBuilder withObject(String fieldKey, Map<String, Object> nestedObject) {
            values.put(fieldKey, nestedObject);
            return this;
        }

        public ObjectBuilder withList(String fieldKey, List<?> list) {
            values.put(fieldKey, list);
            return this;
        }

        public ObjectBuilder withStringList(String fieldKey, List<String> list) {
            values.put(fieldKey, list);
            return this;
        }

        public ObjectBuilder withNumberList(String fieldKey, List<? extends Number> list) {
            values.put(fieldKey, list);
            return this;
        }

        public void build() {
            if (parentPath == null) {
                config.addObject(key, values);
            } else {
                config.addObjectAt(parentPath, key, values);
            }
        }
    }

    public static class SchemaBuilder {
        private final SJNAConfig config;
        private final String schemaName;
        private final String key;
        private final Map<String, Object> values = new LinkedHashMap<>();

        SchemaBuilder(SJNAConfig config, String schemaName, String key) {
            this.config = config;
            this.schemaName = schemaName;
            this.key = key;
        }

        public SchemaBuilder with(String fieldKey, Object value) {
            values.put(fieldKey, value);
            return this;
        }

        public SchemaBuilder withString(String fieldKey, String value) {
            values.put(fieldKey, value);
            return this;
        }

        public SchemaBuilder withNumber(String fieldKey, Number value) {
            values.put(fieldKey, value);
            return this;
        }

        public SchemaBuilder withInt(String fieldKey, int value) {
            values.put(fieldKey, value);
            return this;
        }

        public SchemaBuilder withBoolean(String fieldKey, boolean value) {
            values.put(fieldKey, value);
            return this;
        }

        public SchemaBuilder withObject(String fieldKey, Map<String, Object> nestedObject) {
            values.put(fieldKey, nestedObject);
            return this;
        }

        public SchemaBuilder withList(String fieldKey, List<?> list) {
            values.put(fieldKey, list);
            return this;
        }

        public SchemaBuilder withStringList(String fieldKey, List<String> list) {
            values.put(fieldKey, list);
            return this;
        }

        public SchemaBuilder withNumberList(String fieldKey, List<? extends Number> list) {
            values.put(fieldKey, list);
            return this;
        }

        public void build() {
            config.addFromSchema(schemaName, key, values);
        }
    }
}