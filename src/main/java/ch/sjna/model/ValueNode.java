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
        return value.toString();
    }

    public Number asNumber() {
        return (Number) value;
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    public ObjectNode asObject() {
        return (ObjectNode) value;
    }

    @SuppressWarnings("unchecked")
    public List<ValueNode> asList() {
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
