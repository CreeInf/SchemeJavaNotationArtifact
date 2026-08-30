package ch.sjna.claude.model;

public interface Node {
    NodeType getType();
    String getComment();
    void setComment(String comment);
}
