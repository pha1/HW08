package edu.uncc.hw08;

import java.io.Serializable;

public class Chat implements Serializable {
    String chat_id;
    String user1;
    String user2;
    String lastMessageSent;
    String lastMessageCreatedAt;
    String user1name;
    String user2name;

    public String getUser1name() {
        return user1name;
    }

    public String getUser2name() {
        return user2name;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public String getLastMessageSent() {
        return lastMessageSent;
    }

    public String getLastMessageCreatedAt() {
        return lastMessageCreatedAt;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chat_id='" + chat_id + '\'' +
                ", user1='" + user1 + '\'' +
                ", user2='" + user2 + '\'' +
                ", lastMessageSent='" + lastMessageSent + '\'' +
                ", lastMessageCreatedAt='" + lastMessageCreatedAt + '\'' +
                ", user1name='" + user1name + '\'' +
                ", user2name='" + user2name + '\'' +
                '}';
    }
}
