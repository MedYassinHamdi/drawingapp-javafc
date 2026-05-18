package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleShape implements ShapeModel {
    private Rectangle rectangle;

    // Constructeur qui prend les coins x1,y1 et x2,y2
    public RectangleShape(double x1, double y1, double x2, double y2) {
        rectangle = new Rectangle();
        rectangle.setX(Math.min(x1, x2));
        rectangle.setY(Math.min(y1, y2));
        rectangle.setWidth(Math.abs(x2 - x1));
        rectangle.setHeight(Math.abs(y2 - y1));
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);
    }

    @Override
    public Shape getShape() {
        return rectangle;
    }
}
