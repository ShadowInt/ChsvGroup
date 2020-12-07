package com.traynotifications.animations;

import javafx.util.Duration;

public interface TrayAnimation {

    AnimationType getAnimationType();

    void playSequential(Duration dismissDelay);

    void playShowAnimation();

    void playDismissAnimation();

    boolean isShowing();
}