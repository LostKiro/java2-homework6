package ru.geekbrains.authentication;

import ru.geekbrains.models.Users;

import java.util.List;

public class BaseAuthenticationService implements AuthenticationService {

    private static final List<Users> clients = List.of(
            new Users("login1", "1111", "Pavel"),
            new Users("login2", "2222", "Masha"),
            new Users("login3", "3333", "Valya")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (Users client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");
    }
}

