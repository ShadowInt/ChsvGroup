package com.client.chatwindow;


import com.client.util.VoicePlayback;
import com.client.util.VoiceRecorder;
import com.client.util.VoiceUtil;
import com.messages.Message;
import com.messages.MessageType;
import com.messages.bubble.BubbleSpec;
import com.messages.bubble.BubbledLabel;
import com.traynotifications.animations.AnimationType;
import com.traynotifications.notification.TrayNotification;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.media.MediaPlayer;

public class ChatController implements Initializable {

    public ScrollPane scrollPane;
    @FXML private TextArea messageBox;
    @FXML private Label nicknameLabel;
    @FXML private Label userCountLabel;
    @FXML private ImageView userAvatarIcon;
    @FXML ListView chatPane;
    @FXML BorderPane borderPane;
    @FXML ImageView microphoneIcon;

    Image micActiveIcon = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    Image micInactiveIcon = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());

    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            Listener.send(msg);
            messageBox.clear();
        }
    }

    public void recordVoiceMessage() throws IOException {
        if (VoiceUtil.isRecording()) {
            Platform.runLater(() -> {
                        microphoneIcon.setImage(micInactiveIcon);
                    }
            );
            VoiceUtil.setRecording(false);
        } else {
            Platform.runLater(() -> {
                        microphoneIcon.setImage(micActiveIcon);

                    }
            );
            VoiceRecorder.captureAudio();
        }
    }


    public synchronized void addToChat(Message msg) {
        Task<HBox> otherMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image avatar = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture().toLowerCase() + ".png").toString());
                ImageView userAvatar = new ImageView(avatar);
                userAvatar.setFitHeight(32);
                userAvatar.setFitWidth(32);
                BubbledLabel bubbledL = new BubbledLabel();
                if (msg.getType() == MessageType.VOICE){
                    ImageView iconSound = new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString()));
                    bubbledL.setGraphic(iconSound);
                    bubbledL.setText("Голосовое сообщение");
                    VoicePlayback.playAudio(msg.getVoiceMsg());
                }else {
                    bubbledL.setText(msg.getName() + ": " + msg.getMsg());
                }
                bubbledL.setBackground(new Background(new BackgroundFill(Color.web("#ecedf1", 1),null, null)));
                HBox x = new HBox();
                bubbledL.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                bubbledL.setTextFill(Color.BLACK);
                x.getChildren().addAll(userAvatar, bubbledL);
                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };

        otherMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(otherMessages.getValue());
        });

        Task<HBox> myMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image avatar = userAvatarIcon.getImage();
                ImageView userAvatar = new ImageView(avatar);
                userAvatar.setFitHeight(32);
                userAvatar.setFitWidth(32);

                BubbledLabel bubbledL = new BubbledLabel();
                if (msg.getType() == MessageType.VOICE){
                    bubbledL.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString())));
                    bubbledL.setText("Голосовое сообщение");
                    VoicePlayback.playAudio(msg.getVoiceMsg());
                } else {
                    bubbledL.setText(msg.getMsg());
                }
                bubbledL.setBackground(new Background(new BackgroundFill(Color.web("#cce4ff", 1),
                        null, null)));
                HBox hBox = new HBox();
                hBox.setMaxWidth(chatPane.getWidth() - 20);
                hBox.setAlignment(Pos.TOP_RIGHT);
                bubbledL.setTextFill(Color.BLACK);
                bubbledL.setBorder(Border.EMPTY);
                bubbledL.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                hBox.getChildren().addAll(bubbledL, userAvatar);

                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return hBox;
            }
        };
        myMessages.setOnSucceeded(event -> chatPane.getItems().add(myMessages.getValue()));

        if (msg.getName().equals(nicknameLabel.getText())) {
            Thread thread = new Thread(myMessages);
            thread.setDaemon(true);
            thread.start();
        } else {
            Thread thread = new Thread(otherMessages);
            thread.setDaemon(true);
            thread.start();
        }
    }
    public void setNicknameLabel(String username) {
        this.nicknameLabel.setText(username);
    }

    public void setImageLabel() throws IOException {
        this.userAvatarIcon.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
    }

    public void setOnlineLabel(String usercount) {
        Platform.runLater(() -> userCountLabel.setText(usercount));
    }

    public void setUserList(Message msg) {
        Platform.runLater(() -> {
            setOnlineLabel(String.valueOf(msg.getUserlist().size()));
        });
    }

    public void newUserNotification(Message msg) {
        Platform.runLater(() -> {
            Image profileImg = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture().toLowerCase() +".png").toString(),50,50,false,false);
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setTitle("Присоединился новый пользователь!");
            trayNotification.setMessage(msg.getName() + " присоединился к ЧСВ-чату!");
            trayNotification.setRectangleFill(Paint.valueOf("#2C3E50"));
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setImage(profileImg);
            trayNotification.showAndDismiss(Duration.seconds(5));
            try {
                Media media = new Media(getClass().getClassLoader().getResource("sounds/notification.wav").toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setImageLabel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public void logoutScene() {
        Platform.exit();
        System.exit(0);
    }
}