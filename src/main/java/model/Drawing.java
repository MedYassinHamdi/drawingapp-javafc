package model;

import java.util.ArrayList;
import java.util.List;

public class Drawing {
    private List<ShapeModel> shapes = new ArrayList<>();
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addShape(ShapeModel shape) {
        shapes.add(shape);
    }

    public List<ShapeModel> getShapes() {
        return shapes;
    }

    public void clear() {
        shapes.clear();
    }

}
