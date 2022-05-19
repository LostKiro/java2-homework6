package ru.geekbrains.handler;

import ru.geekbrains.Server;
import ru.geekbrains.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // msg
    private static final String STOP_SERVER_MSG_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_MSG_CMD_PREFIX = "/end";

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.username = "";

            new Thread(() -> {
                try {
                    while (true) {
                        authentication();
                        readMessage();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);// отписка ClientHandler от списка клиентов. Что бы не летели ошибки
                    // на сервере, когда закрываешь окно чата.
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = server.getAuthenticationService();

        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null) {
            if (!server.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }
            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            server.subscribe(this);
            System.out.println("Пользователь | " + username + " подключился к чату");
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не верны");
            return false;
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(String.format("%s %s", CLIENT_MSG_CMD_PREFIX, message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_MSG_CMD_PREFIX)) {
                System.exit(0);
            } else if (message.startsWith(END_CLIENT_MSG_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                String[] messageInChat = message.split(" ");
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 2 ; i < messageInChat.length ; i++) {
                    stringBuffer.append(messageInChat[i] + " ");
                }
                server.privateMessage(messageInChat[1], stringBuffer.toString());
            } else {
                server.broadcastMessage(username + " " + message);
            }
        }
    }
    private void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(username + " вышел из чата");
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

