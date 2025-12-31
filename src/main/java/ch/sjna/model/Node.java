package ch.sjna.model;

public interface Node {
    NodeType getType();
    String getComment();
    void setComment(String comment);
}
