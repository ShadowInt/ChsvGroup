package com.traynotifications.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import com.traynotifications.models.CustomStage;

public class FadeAnimation implements TrayAnimation {

    private final Timeline showAnimation, dismissAnimation;
    private final SequentialTransition sq;
    private final CustomStage stage;
    private boolean trayIsShowing;

    public FadeAnimation(CustomStage customStage) {

        this.stage = customStage;

        showAnimation = setupShowAnimation();
        dismissAnimation = setupDismissAnimation();

        sq = new SequentialTransition(setupShowAnimation(), setupDismissAnimation());
    }

    private Timeline setupShowAnimation() {

        Timeline tl = new Timeline();

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, kvOpacity);

        KeyValue kvOpacity2 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame frame2 = new KeyFrame(Duration.millis(3000), kvOpacity2);

        tl.getKeyFrames().addAll(frame1, frame2);

        tl.setOnFinished(e -> trayIsShowing = true);

        return tl;
    }

    private Timeline setupDismissAnimation() {

        Timeline tl = new Timeline();

        KeyValue kv1 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf1 = new KeyFrame(Duration.millis(2000), kv1);

        tl.getKeyFrames().addAll(kf1);

        tl.setOnFinished(e -> {
            trayIsShowing = false;
            stage.close();
            stage.setLocation(stage.getBottomRight());
        });

        return tl;
    }

    @Override
    public AnimationType getAnimationType() {
        return AnimationType.FADE;
    }

    @Override
    public void playSequential(Duration dismissDelay) {
        sq.getChildren().get(1).setDelay(dismissDelay);
        sq.play();
    }

    @Override
    public void playShowAnimation() {
        showAnimation.play();
    }

    @Override
    public void playDismissAnimation() {
        dismissAnimation.play();
    }

    @Override
    public boolean isShowing() {
        return trayIsShowing;
    }
}