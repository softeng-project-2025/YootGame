package model.dto;


public class GameMessage {
    private final String text;
    private final MessageType type;

    public GameMessage(String text, MessageType type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public MessageType getType() {
        return type;
    }
}