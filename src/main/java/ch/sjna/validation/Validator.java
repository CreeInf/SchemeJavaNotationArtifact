package ch.sjna.validation;

import ch.sjna.model.*;
import java.util.*;

public class Validator {
    private final List<ValidationError> errors = new ArrayList<>();

    public ValidationResult validate(Document doc) {
        errors.clear();

        for (Map.Entry<String, Node> entry : doc.getRoot().entrySet()) {
            if (entry.getValue() instanceof PropertyNode) {
                validateProperty((PropertyNode) entry.getValue());
            }
        }

        return new ValidationResult(new ArrayList<>(errors));
    }

    public ValidationResult validateAgainstSchema(Document doc, String schemaName) {
        errors.clear();

        SchemaDefinition schema = doc.getSchema(schemaName);
        if (schema == null) {
            errors.add(new ValidationError("Schema not found: " + schemaName));
            return new ValidationResult(new ArrayList<>(errors));
        }

        for (Map.Entry<String, SchemaProperty> entry : schema.getProperties().entrySet()) {
            String key = entry.getKey();
            SchemaProperty schemaProp = entry.getValue();
            Node node = doc.get(key);

            if (node == null) {
                errors.add(new ValidationError("Missing required property: " + key));
                continue;
            }

            if (node instanceof PropertyNode) {
                validatePropertyAgainstSchema((PropertyNode) node, schemaProp);
            }
        }

        return new ValidationResult(new ArrayList<>(errors));
    }

    private void validateProperty(PropertyNode prop) {
        if (prop.hasEnum()) {
            String value = prop.getValue().asString();
            if (!prop.getEnumDefinition().isValid(value)) {
                errors.add(new ValidationError(
                        "Invalid enum value for '" + prop.getKey() + "': " + value +
                                ". Allowed: " + prop.getEnumDefinition().getOptions()
                ));
            }
        }

        if (prop.getValue().getValueType() == ValueNode.ValueType.OBJECT) {
            ObjectNode obj = prop.getValue().asObject();
            for (PropertyNode child : obj.getProperties().values()) {
                validateProperty(child);
            }
        }
    }

    private void validatePropertyAgainstSchema(PropertyNode prop, SchemaProperty schemaProp) {
        if (schemaProp.hasEnum()) {
            String value = prop.getValue().asString();
            if (!schemaProp.getEnumDefinition().isValid(value)) {
                errors.add(new ValidationError(
                        "Schema violation for '" + prop.getKey() + "': " + value +
                                ". Expected one of: " + schemaProp.getEnumDefinition().getOptions()
                ));
            }
        }
    }
}
