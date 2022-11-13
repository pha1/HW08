package edu.uncc.hw08;

public class Message {
    String message_text;
    String created_At;
    String user_id;
    String message_id;
    String user_name;

    public String getUser_name() {
        return user_name;
    }

    public String getMessage_text() {
        return message_text;
    }

    public String getCreated_At() {
        return created_At;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_text='" + message_text + '\'' +
                ", created_At='" + created_At + '\'' +
                ", user_id='" + user_id + '\'' +
                ", message_id='" + message_id + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
