package model.dto;


public class GameMessage {
    private final String content;
    private final MessageType type;

    public GameMessage(String content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }
}