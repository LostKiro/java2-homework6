package ru.geekbrains;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидание подключения клиентов..");
            Socket socket = serverSocket.accept();
            while (true) {
               int date = socket.getInputStream().read();
                System.out.println((char)date);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
