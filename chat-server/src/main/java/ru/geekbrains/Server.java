package ru.geekbrains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидание подключения клиентов..");

            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());


            Thread trIn = new Thread(() -> {
                System.out.println("Клиент подключился");
                try {
                    while (true) {
                        if (!socket.isConnected()) {
                            System.out.println("Клиент не подключился");
                            break;
                        }
                        String inputMessage = in.readUTF();
                        if (inputMessage.equals("/end")) {
                            System.out.println("Клиент отключился");
                            break;
                        }
                        System.out.println("Клиент сказал:" + inputMessage);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread trOut = new Thread(() -> {
                Scanner sc = new Scanner(System.in);
                try {
                    while (true) {
                        if (!socket.isConnected()) {
                            break;
                        }
                        String inputMessage = sc.nextLine();
                        out.writeUTF(inputMessage);

                        if (inputMessage.equals("/end")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            trIn.start();
            trOut.start();

            try {
                trIn.join();
                trOut.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
