package command;

import javafx.scene.layout.Pane;
import model.Drawing;
import model.ShapeModel;

public class AddShapeCommand implements DrawCommand {

    private ShapeModel shape;
    private Drawing drawing;
    private Pane drawingPane;

    public AddShapeCommand(ShapeModel shape, Drawing drawing, Pane drawingPane) {
        this.shape = shape;
        this.drawing = drawing;
        this.drawingPane = drawingPane;
    }

    @Override
    public void execute() {
        drawing.addShape(shape);
        drawingPane.getChildren().add(shape.getShape());
    }

    @Override
    public void undo() {
        drawing.getShapes().remove(shape);
        drawingPane.getChildren().remove(shape.getShape());
    }
}