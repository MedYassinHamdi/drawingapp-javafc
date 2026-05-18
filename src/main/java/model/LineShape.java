package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class LineShape implements ShapeModel {
    private Line line;

    public LineShape(double startX, double startY, double endX, double endY) {
        line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.BLACK);
    }

    @Override
    public Shape getShape() {
        return line;
    }
}
