package ru.geekbrains;

import ru.geekbrains.authentication.AuthenticationService;
import ru.geekbrains.authentication.BaseAuthenticationService;
import ru.geekbrains.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private AuthenticationService authenticationService;
    private List<ClientHandler> clients;

    public Server() {
        try {
            this.clients = new ArrayList<>();
            this.serverSocket = new ServerSocket(8189);
            this.authenticationService = new BaseAuthenticationService();
            System.out.println("Сервер запущен. Ожидание подключения клиентов..");
            System.out.println("--------------------");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключился новый клиент..");
                ClientHandler c = new ClientHandler(this, socket);
                subscribe(c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (authenticationService != null) {
                authenticationService.endAuthentication();
            }
        }
    }

    public synchronized void subscribe(ClientHandler c) {
        clients.add(c);
    }

    public synchronized void unsubscribe(ClientHandler c) {
        clients.remove(c);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public synchronized void privateMessage(String username, String message) {
        ClientHandler c = clients.stream()
                .filter(x -> x.getUsername().equals(username))
                .findFirst()
                .orElseThrow();

                c.sendMessage(message);
    }

        public AuthenticationService getAuthenticationService() {
            return authenticationService;
        }

        public synchronized boolean isUsernameBusy (String username){
            for (ClientHandler c : clients) {
                if (c.getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }
    }
