package ch.sjna.claude.model;

public class PropertyNode implements Node {
    private final String key;
    private final ValueNode value;
    private final EnumDefinition enumDef;
    private String comment;

    public PropertyNode(String key, ValueNode value, EnumDefinition enumDef) {
        this.key = key;
        this.value = value;
        this.enumDef = enumDef;
    }

    public String getKey() {
        return key;
    }

    public ValueNode getValue() {
        return value;
    }

    public EnumDefinition getEnumDefinition() {
        return enumDef;
    }

    public boolean hasEnum() {
        return enumDef != null;
    }

    @Override
    public NodeType getType() {
        return NodeType.PROPERTY;
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
