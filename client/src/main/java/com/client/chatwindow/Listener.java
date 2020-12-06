package com.client.chatwindow;

import com.client.login.LoginController;
import com.messages.Message;
import com.messages.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

import static com.messages.MessageType.CONNECTED;

public class Listener implements Runnable{

    private static String avatar;
    private Socket socket;
    public String host;
    public int port;
    public static String nickname;
    public ChatController controller;
    private static ObjectOutputStream objectOutputStream;
    private ObjectInputStream input;
    Logger logger = LogManager.getLogger(Listener.class);

    public Listener(String host, int port, String nickname, String avatar, ChatController controller) {
        this.host = host;
        this.port = port;
        Listener.nickname = nickname;
        Listener.avatar = avatar;
        this.controller = controller;
    }

    public void run() {
        try {
            socket = new Socket(host, port);
            LoginController.getInstance().showScene();
            OutputStream outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            input = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            logger.error("Не может подключиться");
        }
        logger.info("Подключение принято " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            connect();
            logger.info("Сокеты готовы!");
            while (socket.isConnected()) {
                Message message = null;
                message = (Message) input.readObject();

                if (message != null) {
                    logger.debug("Сообщение получено:" + message.getMsg() + " Тип сообщения:" + message.getType() + "Имя:" + message.getName());
                    switch (message.getType()) {
                        case USER:
                        case VOICE:
                            controller.addToChat(message);
                            break;
                        case CONNECTED:
                        case DISCONNECTED:
                            controller.setUserList(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.logoutScene();
        }
    }

    /* Этот метод используется для отправки обычного сообщения
     * @param msg - Сообщение, которое отправляет пользователь
     */
    public static void send(String msg) throws IOException {
        Message message = new Message();
        message.setName(nickname);
        message.setType(MessageType.USER);
        message.setMsg(msg);
        message.setPicture(avatar);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    /* Этот метод используется для отправки голосового сообщения
     * @param msg - Сообщение, которое отправляет пользователь
     */
    public static void sendVoiceMessage(byte[] audio) throws IOException {
        Message message = new Message();
        message.setName(nickname);
        message.setType(MessageType.VOICE);
        message.setVoiceMsg(audio);
        message.setPicture(avatar);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    /* Этот метод используется для отправки сообщения о подключении */
    public static void connect() throws IOException {
        Message message = new Message();
        message.setName(nickname);
        message.setType(CONNECTED);
        message.setMsg("подключился");
        message.setPicture(avatar);
        objectOutputStream.writeObject(message);
    }

}

