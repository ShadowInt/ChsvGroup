package com.messages.bubble;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class BubbledLabel extends Label {

    private BubbleSpec bubbleSpec = BubbleSpec.FACE_LEFT_CENTER;
    private boolean systemCall = false;

    public BubbledLabel() {
        super();
        init();
    }

    private void init(){
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(1.3);
        dropShadow.setOffsetY(1.3);
        dropShadow.setColor(Color.GREEN);
        setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
        shapeProperty().addListener(new ChangeListener<Shape>() {
            @Override
            public void changed(ObservableValue<? extends Shape> arg0,
                                Shape arg1, Shape arg2) {
                if(systemCall){
                    systemCall = false;
                }else{
                    shapeIt();
                }
            }
        });

        heightProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable arg0) {
                if(!systemCall)
                    setPrefHeight(Label.USE_COMPUTED_SIZE);
            }
        });

        widthProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                if(!systemCall)
                    setPrefHeight(Label.USE_COMPUTED_SIZE);
            }
        });

        shapeIt();
    }

    @Override
    protected void updateBounds() {
        super.updateBounds();
        double padding = 8.0;
        switch (bubbleSpec) {
            case FACE_LEFT_BOTTOM:
                setPadding(new Insets(padding, padding,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                + padding,
                        padding));
                break;
            case FACE_LEFT_CENTER:
                setPadding(new Insets(padding, padding, padding,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                + padding
                ));
                break;
            case FACE_RIGHT_BOTTOM:
            case FACE_RIGHT_CENTER:
                setPadding(new Insets(padding,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                + padding
                        , padding, padding));
                break;
            case FACE_TOP:
                setPadding(new Insets(
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                + padding,
                        padding, padding, padding));
                break;

            default:
                break;
        }
    }

    public BubbleSpec getBubbleSpec() {
        return bubbleSpec;
    }

    public void setBubbleSpec(BubbleSpec bubbleSpec) {
        this.bubbleSpec = bubbleSpec;
        shapeIt();
    }

    private void shapeIt(){
        systemCall = true;
        setShape(new Bubble(bubbleSpec));
        System.gc();
    }
}
