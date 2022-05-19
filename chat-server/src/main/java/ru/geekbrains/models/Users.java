package ru.geekbrains.models;

import lombok.Data;

@Data

public class Users {
    private final String login;
    private final String password;
    private final String username;
}
