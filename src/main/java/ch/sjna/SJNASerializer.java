package ch.sjna;

import ch.sjna.model.*;
import java.util.*;

public class SJNASerializer {
    private StringBuilder sb;
    private int indentLevel;
    private static final String INDENT = "    ";

    public String serialize(Document doc) {
        sb = new StringBuilder();
        indentLevel = 0;

        // Schemas zuerst
        for (Map.Entry<String, SchemaDefinition> entry : doc.getSchemas().entrySet()) {
            serializeSchema(entry.getValue());
            sb.append("\n");
        }

        // Dann Properties
        for (Map.Entry<String, Node> entry : doc.getRoot().entrySet()) {
            if (entry.getValue() instanceof PropertyNode) {
                serializeProperty(entry.getKey(), (PropertyNode) entry.getValue());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private void serializeSchema(SchemaDefinition schema) {
        sb.append("/: schema: ").append(schema.getName()).append(" {\n");
        indentLevel++;

        for (SchemaProperty prop : schema.getProperties().values()) {
            indent();
            sb.append(escapeKey(prop.getKey()));

            if (prop.hasEnum()) {
                sb.append("(");
                List<String> options = prop.getEnumDefinition().getOptions();
                for (int i = 0; i < options.size(); i++) {
                    sb.append('"').append(escapeString(options.get(i))).append('"');
                    if (i < options.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }

            sb.append(":");

            if (prop.getDescription() != null && !prop.getDescription().isEmpty()) {
                sb.append(" //").append(prop.getDescription());
            }

            sb.append(";\n");
        }

        indentLevel--;
        sb.append("}\n");
    }

    private void serializeProperty(String key, PropertyNode prop) {
        indent();
        sb.append(escapeKey(key));

        if (prop.hasEnum()) {
            sb.append("(");
            List<String> options = prop.getEnumDefinition().getOptions();
            for (int i = 0; i < options.size(); i++) {
                sb.append('"').append(escapeString(options.get(i))).append('"');
                if (i < options.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

        sb.append(": ");
        serializeValue(prop.getValue());
        sb.append(";");
    }

    private void serializeValue(ValueNode value) {
        switch (value.getValueType()) {
            case STRING:
                sb.append('"').append(escapeString(value.asString())).append('"');
                break;
            case NUMBER:
                sb.append(value.asNumber());
                break;
            case BOOLEAN:
                sb.append(value.asBoolean());
                break;
            case IDENTIFIER:
                sb.append(value.asString());
                break;
            case OBJECT:
                serializeObject(value.asObject());
                break;
            case LIST:
                serializeList(value.asList());
                break;
        }
    }

    private void serializeList(List<ValueNode> items) {
        sb.append("[");
        for (int i = 0; i < items.size(); i++) {
            serializeValue(items.get(i));
            if (i < items.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private void serializeObject(ObjectNode obj) {
        sb.append("{\n");
        indentLevel++;

        for (Map.Entry<String, PropertyNode> entry : obj.getProperties().entrySet()) {
            serializeProperty(entry.getKey(), entry.getValue());
            sb.append("\n");
        }

        indentLevel--;
        indent();
        sb.append("}");
    }

    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            sb.append(INDENT);
        }
    }

    private String escapeKey(String key) {
        if (key.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return key;
        } else {
            return '"' + escapeString(key) + '"';
        }
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\r", "\\r");
    }
}