package ru.geekbrains.authentication;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password);

    void startAuthentication();
    void endAuthentication();
}
