package ch.sjna.model;

public class SchemaProperty {
    private final String key;
    private final EnumDefinition enumDef;
    private final String description;

    public SchemaProperty(String key, EnumDefinition enumDef, String description) {
        this.key = key;
        this.enumDef = enumDef;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public EnumDefinition getEnumDefinition() {
        return enumDef;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasEnum() {
        return enumDef != null;
    }
}
