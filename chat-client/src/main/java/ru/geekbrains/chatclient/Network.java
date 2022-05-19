package ru.geekbrains.chatclient;

public class Network {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // msg
    private static final String STOP_SERVER_MSG_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_MSG_CMD_PREFIX = "/end";
}
