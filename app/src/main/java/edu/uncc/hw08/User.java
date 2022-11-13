package edu.uncc.hw08;

public class User {
    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean isLogged_in() {
        return logged_in;
    }

    String name, user_id;
    boolean logged_in;

    @Override
    public String toString() {
        return name;
    }
}
