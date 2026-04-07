package ch.sjna;

import ch.sjna.model.*;
import ch.sjna.parser.*;
import ch.sjna.validation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SJNA {
    public static Document load(String filePath) throws IOException, ParseException {
        String content = Files.readString(Paths.get(filePath));
        return parse(content);
    }

    public static Document parse(String content) throws ParseException {
        SJNAParser parser = new SJNAParser();
        return parser.parse(content);
    }

    public static void save(Document doc, String filePath) throws IOException {
        String content = serialize(doc);
        Files.writeString(Paths.get(filePath), content);
    }

    public static String serialize(Document doc) {
        StringBuilder sb = new StringBuilder();
        SJNASerializer serializer = new SJNASerializer();
        return serializer.serialize(doc);
    }

    public static ValidationResult validate(Document doc) {
        Validator validator = new Validator();
        return validator.validate(doc);
    }

    public static ValidationResult validateAgainstSchema(Document doc, String schemaName) {
        Validator validator = new Validator();
        return validator.validateAgainstSchema(doc, schemaName);
    }

    public static SJNAConfig asConfig(Document doc) {
        return new SJNAConfig(doc);
    }

    public static ObjectNode createFromSchema(Document doc, String schemaName) {
        SchemaDefinition schema = doc.getSchema(schemaName);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + schemaName);
        }
        return schema.createInstance();
    }

    public static ObjectNode createFromSchema(Document doc, String schemaName, Map<String, Object> values) {
        SchemaDefinition schema = doc.getSchema(schemaName);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + schemaName);
        }
        return schema.createInstance(values);
    }

    public static PropertyNode createPropertyFromSchema(Document doc, String schemaName, String propertyKey,
            Map<String, Object> values) {
        ObjectNode obj = createFromSchema(doc, schemaName, values);
        return new PropertyNode(propertyKey, new ValueNode(obj, ValueNode.ValueType.OBJECT), null);
    }
    public static void insertSchema(Document schemaDoc, String schemaName, Document targetDoc, String key, Map<String, Object> values) {
        SchemaDefinition schema = schemaDoc.getSchema(schemaName);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + schemaName);
        }
        ObjectNode instance = schema.createInstance(values);
        PropertyNode prop = new PropertyNode(key, new ValueNode(instance, ValueNode.ValueType.OBJECT), null);
        targetDoc.addProperty(key, prop);
    }
}