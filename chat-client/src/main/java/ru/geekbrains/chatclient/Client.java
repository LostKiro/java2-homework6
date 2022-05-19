package ru.geekbrains.chatclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable {

    @FXML
    TextArea chatArea;

    @FXML
    TextField messageField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String inputMessage = in.readUTF();
                        chatArea.appendText(inputMessage + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

        } catch (IOException e) {
            System.out.println("Невозможно подключиться к серверу");
            System.exit(0);
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}