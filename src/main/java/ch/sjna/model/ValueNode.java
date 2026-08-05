package ch.sjna.model;


import java.util.*;

public class ValueNode implements Node {
    private final Object value;
    private final ValueType valueType;
    private String comment;

    public enum ValueType {
        STRING, NUMBER, BOOLEAN, IDENTIFIER, OBJECT, LIST
    }

    public ValueNode(Object value, ValueType type) {
        this.value = value;
        this.valueType = type;
    }

    public Object getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String asString() {
        if (value == null) return null;
        return value.toString();
    }

    public Number asNumber() {
        if (value == null) return null;
        return (Number) value;
    }

    public Boolean asBoolean() {
        if (value == null) return null;
        return (Boolean) value;
    }

    public ObjectNode asObject() {
        if (value == null) return null;
        return (ObjectNode) value;
    }

    public List<ValueNode> asList() {
        if (value == null) return null;
        return (List<ValueNode>) value;
    }

    @Override
    public NodeType getType() {
        return NodeType.VALUE;
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
