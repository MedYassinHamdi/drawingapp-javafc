package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CircleShape implements ShapeModel {
    private Circle circle;

    public CircleShape(double centerX, double centerY, double radius) {
        circle = new Circle(centerX, centerY, radius);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
    }

    @Override
    public Shape getShape() {
        return circle;
    }
}
