package factory;

import model.*;

public class ShapeFactory {

    public static ShapeModel createShape(ShapeType type, double startX, double startY, double endX, double endY) {
        switch(type) {
            case RECTANGLE:
                return new RectangleShape(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
            case CIRCLE:
                double radius = Math.hypot(endX - startX, endY - startY);
                return new CircleShape(startX, startY, radius);
            case LINE:
                return new LineShape(startX, startY, endX, endY);
            default:
                throw new IllegalArgumentException("Type de forme non supporté");
        }
    }
}
