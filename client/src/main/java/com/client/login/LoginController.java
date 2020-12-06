package com.client.login;

import com.client.chatwindow.ChatController;
import com.client.chatwindow.Listener;
import com.client.util.ResizeHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoginController {
    @FXML public  TextField hostnameTextfield;
    @FXML private TextField portTextfield;
    @FXML private TextField usernameTextfield;
    public static ChatController chatController;
    private Scene scene;

    Logger logger = LogManager.getLogger(LoginController.class);

    private static LoginController instance;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }
    public void loginButtonAction() throws IOException {
        String hostname = hostnameTextfield.getText();
        int port = Integer.parseInt(portTextfield.getText());
        String username = usernameTextfield.getText();
        String picture = "Default";

        if (username.length() <= 255){
            if (username.matches("^[^\\d\\s]+$")){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
                Parent window = (Pane) fxmlLoader.load();
                chatController = fxmlLoader.<ChatController>getController();
                Listener listener = new Listener(hostname, port, username, picture, chatController);
                Thread thread = new Thread(listener);
                thread.start();
                this.scene = new Scene(window);
            } else {
                logger.error("Никнейм содержит пробелы или символы");
            }
        } else {
            logger.error("Никнейм больше 255 символов");
        }
    }

    public void showScene() throws IOException {
        Platform.runLater(() -> {
            Stage stage = (Stage) hostnameTextfield.getScene().getWindow();
            stage.setResizable(true);
            stage.setWidth(1040);
            stage.setHeight(620);

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(this.scene);
            stage.setMinWidth(800);
            stage.setMinHeight(300);
            ResizeHelper.addResizeListener(stage);
            stage.centerOnScreen();
            chatController.setNicknameLabel(usernameTextfield.getText());
        });
    }
}
