package model;

import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.Map;
import java.util.LinkedHashMap;

public class DrawingDAO {
	
	
	
	public Map<Integer, String> getDrawingsList() throws SQLException {
	    Map<Integer, String> drawingsMap = new LinkedHashMap<>();
	    try (Connection conn = DBUtil.getConnection()) {
	        String sql = "SELECT id, name FROM drawings ORDER BY created_at DESC";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            drawingsMap.put(rs.getInt("id"), rs.getString("name"));
	        }
	    }
	    return drawingsMap;
	}
	public void saveDrawing(Drawing drawing) throws SQLException {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        conn = DBUtil.getConnection();
	        conn.setAutoCommit(false);

	        // Insérer le dessin avec nom et date
	        String insertDrawingSQL = "INSERT INTO drawings (name, created_at) VALUES (?, NOW())";
	        stmt = conn.prepareStatement(insertDrawingSQL, Statement.RETURN_GENERATED_KEYS);
	        stmt.setString(1, drawing.getName());
	        stmt.executeUpdate();

	        rs = stmt.getGeneratedKeys();
	        int drawingId = -1;
	        if (rs.next()) {
	            drawingId = rs.getInt(1);
	        }

	        // Préparation de la requête pour insérer les formes
	        String insertShapeSQL = "INSERT INTO shapes (drawing_id, type, x1, y1, x2, y2) VALUES (?, ?, ?, ?, ?, ?)";
	        PreparedStatement shapeStmt = conn.prepareStatement(insertShapeSQL);

	        for (ShapeModel shape : drawing.getShapes()) {
	            shapeStmt.setInt(1, drawingId);

	            if (shape instanceof RectangleShape) {
	                shapeStmt.setString(2, "RECTANGLE");
	                RectangleShape r = (RectangleShape) shape;
	                double x = r.getShape().getLayoutX();
	                double y = r.getShape().getLayoutY();
	                double w = ((javafx.scene.shape.Rectangle) r.getShape()).getWidth();
	                double h = ((javafx.scene.shape.Rectangle) r.getShape()).getHeight();
	                shapeStmt.setDouble(3, x);
	                shapeStmt.setDouble(4, y);
	                shapeStmt.setDouble(5, x + w);
	                shapeStmt.setDouble(6, y + h);
	            }
                 else if (shape instanceof CircleShape) {
	                shapeStmt.setString(2, "CIRCLE");
	                CircleShape c = (CircleShape) shape;
	                Circle circle = (Circle) c.getShape();
	                shapeStmt.setDouble(3, circle.getCenterX());
	                shapeStmt.setDouble(4, circle.getCenterY());
	                shapeStmt.setDouble(5, circle.getRadius());
	                shapeStmt.setDouble(6, 0);

	            } else if (shape instanceof LineShape) {
	                shapeStmt.setString(2, "LINE");
	                LineShape l = (LineShape) shape;
	                Line line = (Line) l.getShape();
	                shapeStmt.setDouble(3, line.getStartX());
	                shapeStmt.setDouble(4, line.getStartY());
	                shapeStmt.setDouble(5, line.getEndX());
	                shapeStmt.setDouble(6, line.getEndY());
	            }

	            shapeStmt.addBatch();
	        }

	        shapeStmt.executeBatch();
	        conn.commit();

	    } catch (SQLException e) {
	        if (conn != null) conn.rollback();
	        throw e;
	    } finally {
	        if (rs != null) rs.close();
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
	    }
	}

	public Drawing loadDrawing(int drawingId) throws SQLException {
	    Drawing drawing = new Drawing();
	    try (Connection conn = DBUtil.getConnection()) {
	        // Récupérer le nom du dessin
	        String sqlName = "SELECT name FROM drawings WHERE id = ?";
	        PreparedStatement psName = conn.prepareStatement(sqlName);
	        psName.setInt(1, drawingId);
	        ResultSet rsName = psName.executeQuery();
	        if (rsName.next()) {
	            drawing.setName(rsName.getString("name"));
	        }

	        // Récupérer les formes
	        String sqlShapes = "SELECT * FROM shapes WHERE drawing_id = ?";
	        PreparedStatement psShapes = conn.prepareStatement(sqlShapes);
	        psShapes.setInt(1, drawingId);
	        ResultSet rsShapes = psShapes.executeQuery();

	        while (rsShapes.next()) {
	            String type = rsShapes.getString("type");
	            double x1 = rsShapes.getDouble("x1");
	            double y1 = rsShapes.getDouble("y1");
	            double x2 = rsShapes.getDouble("x2");
	            double y2 = rsShapes.getDouble("y2");

	            ShapeModel shape = null;
	            switch (type) {
	            case "RECTANGLE":
	                shape = new RectangleShape(x1, y1, x2, y2);

	                    break;
	                case "CIRCLE":
	                    shape = new CircleShape(x1, y1, x2);
	                    break;
	                case "LINE":
	                    shape = new LineShape(x1, y1, x2, y2);
	                    break;
	            }

	            if (shape != null) {
	                drawing.addShape(shape);
	            }
	        }
	    }
	    return drawing;
	}


}
