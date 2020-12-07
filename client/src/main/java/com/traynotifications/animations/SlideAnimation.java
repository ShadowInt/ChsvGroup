package com.traynotifications.animations;

import javafx.animation.*;
import javafx.util.Duration;
import com.traynotifications.models.CustomStage;

public class SlideAnimation implements TrayAnimation {

    private final Timeline showAnimation, dismissAnimation;
    private final SequentialTransition sq;
    private final CustomStage stage;
    private boolean trayIsShowing;

    public SlideAnimation(CustomStage customStage) {

        this.stage = customStage;

        showAnimation = setupShowAnimation();
        dismissAnimation = setupDismissAnimation();

        sq = new SequentialTransition(setupShowAnimation(), setupDismissAnimation());
    }

    private Timeline setupShowAnimation() {

        Timeline tl = new Timeline();

        double offScreenX = stage.getOffScreenBounds().getX();
        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offScreenX);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, kvX);

        Interpolator interpolator = Interpolator.TANGENT(Duration.millis(300), 50);
        KeyValue kvInter = new KeyValue(stage.xLocationProperty(), stage.getBottomRight().getX(), interpolator);
        KeyFrame frame2 = new KeyFrame(Duration.millis(1300), kvInter);

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame frame3 = new KeyFrame(Duration.ZERO, kvOpacity);

        KeyValue kvOpacity2 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame frame4 = new KeyFrame(Duration.millis(1000), kvOpacity2);

        tl.getKeyFrames().addAll(frame1, frame2, frame3, frame4);

        tl.setOnFinished(e -> trayIsShowing = true);

        return tl;
    }

    private Timeline setupDismissAnimation() {

        Timeline tl = new Timeline();

        double offScreenX = stage.getOffScreenBounds().getX();
        Interpolator interpolator = Interpolator.TANGENT(Duration.millis(300), 50);
        double trayPadding = 3;

        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offScreenX + trayPadding, interpolator);
        KeyFrame frame1 = new KeyFrame(Duration.millis(1400), kvX);

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.4);
        KeyFrame frame2 = new KeyFrame(Duration.millis(2000), kvOpacity);

        tl.getKeyFrames().addAll(frame1, frame2);

        tl.setOnFinished(e -> {
            trayIsShowing = false;
            stage.close();
            stage.setLocation(stage.getBottomRight());
        });

        return tl;
    }

    @Override
    public AnimationType getAnimationType() {
        return AnimationType.SLIDE;
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