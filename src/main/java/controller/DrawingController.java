package controller;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import command.AddShapeCommand;
import command.DrawCommand;
import factory.ShapeFactory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import logger.*;
import model.*;

import java.io.*;
import java.sql.SQLException;

public class DrawingController {

    private BorderPane root = new BorderPane();
    private Pane drawingPane = new Pane();

    private ShapeType currentShapeType = ShapeType.RECTANGLE;
    private double startX, startY;

    private Drawing drawing = new Drawing();
    private LoggerContext loggerContext;

    private DrawingDAO drawingDAO = new DrawingDAO();

    private ComboBox<String> loggerSelector = new ComboBox<>();

    private Button saveBtn     = new Button("Enregistrer");
    private Button loadBtn     = new Button("Charger");
    private Button clearBtn    = new Button("Supprimer les formes");
    private Button clearLogsBtn = new Button("Vider logs DB");
    private Button undoBtn     = new Button("Annuler");

    // Command pattern history stack
    private Deque<DrawCommand> commandHistory = new ArrayDeque<>();

    public DrawingController() {
        setupLogger();
        setupUI();
        setupEvents();
    }

    private void setupLogger() {
        loggerSelector.getItems().addAll("Console", "Fichier", "Base de données");
        loggerSelector.setValue("Console");
        loggerContext = new LoggerContext(new ConsoleLogger());

        loggerSelector.setOnAction(e -> {
            String choice = loggerSelector.getValue();
            switch (choice) {
                case "Console":
                    loggerContext.setStrategy(new ConsoleLogger());
                    loggerContext.log("Stratégie de journalisation changée à Console");
                    break;
                case "Fichier":
                    FileLogger fileLogger = new FileLogger();
                    fileLogger.clearLog();
                    loggerContext.setStrategy(fileLogger);
                    loggerContext.log("Stratégie de journalisation changée à Fichier");
                    break;
                case "Base de données":
                    loggerContext.setStrategy(new DBLogger());
                    loggerContext.log("Stratégie de journalisation changée à Base de données");
                    break;
            }
        });
    }

    public void clearLogsTable() {
        DBLogger logger = new DBLogger();
        logger.clearLogs();
    }

    private void setupUI() {
        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton btnRect = createIconButton("Rectangle", ShapeType.RECTANGLE);
        btnRect.setToggleGroup(toggleGroup);
        btnRect.setSelected(true);

        ToggleButton btnCircle = createIconButton("Cercle", ShapeType.CIRCLE);
        btnCircle.setToggleGroup(toggleGroup);

        ToggleButton btnLine = createIconButton("Ligne", ShapeType.LINE);
        btnLine.setToggleGroup(toggleGroup);

        clearLogsBtn.setOnAction(e -> {
            clearLogsTable();
            loggerContext.log("Table logs vidée.");
        });

        undoBtn.setDisable(true);

        HBox palette = new HBox(10, btnRect, btnCircle, btnLine, loggerSelector,
                saveBtn, loadBtn, undoBtn, clearBtn, clearLogsBtn);
        palette.setPadding(new Insets(10));
        palette.setBackground(new Background(
                new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        root.setTop(palette);
        root.setCenter(drawingPane);

        toggleGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                ToggleButton selected = (ToggleButton) newV;
                currentShapeType = (ShapeType) selected.getUserData();
                loggerContext.log("Forme sélectionnée: " + currentShapeType);
            }
        });
    }

    private ToggleButton createIconButton(String tooltipText, ShapeType shapeType) {
        Shape icon;
        switch (shapeType) {
            case RECTANGLE:
                icon = new javafx.scene.shape.Rectangle(20, 15, Color.TRANSPARENT);
                icon.setStroke(Color.BLACK);
                break;
            case CIRCLE:
                icon = new javafx.scene.shape.Circle(10, Color.TRANSPARENT);
                icon.setStroke(Color.BLACK);
                break;
            case LINE:
                icon = new javafx.scene.shape.Line(0, 15, 20, 0);
                icon.setStroke(Color.BLACK);
                break;
            default:
                icon = new javafx.scene.shape.Rectangle(20, 15, Color.RED);
        }

        ToggleButton button = new ToggleButton();
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltipText));
        button.setUserData(shapeType);
        button.setPrefSize(40, 40);
        return button;
    }

    private void setupEvents() {
        drawingPane.setOnMousePressed(this::onMousePressed);
        drawingPane.setOnMouseReleased(this::onMouseReleased);

        // Undo button
        undoBtn.setOnAction(e -> {
            if (!commandHistory.isEmpty()) {
                DrawCommand cmd = commandHistory.pop();
                cmd.undo();
                undoBtn.setDisable(commandHistory.isEmpty());
                loggerContext.log("Action annulée");
            }
        });

        saveBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nom du dessin");
            dialog.setHeaderText("Veuillez entrer un nom pour le dessin :");
            dialog.setContentText("Nom :");

            dialog.showAndWait().ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    showAlert("Erreur", "Le nom ne peut pas être vide !");
                    return;
                }
                drawing.setName(name);
                try {
                    drawingDAO.saveDrawing(drawing);
                    loggerContext.log("Dessin '" + name + "' sauvegardé en base");
                    showAlert("Succès", "Dessin sauvegardé avec succès !");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la sauvegarde : " + ex.getMessage());
                }
            });
        });

        loadBtn.setOnAction(e -> {
            try {
                Map<Integer, String> drawingsMap = drawingDAO.getDrawingsList();
                if (drawingsMap.isEmpty()) {
                    showAlert("Info", "Aucun dessin sauvegardé en base.");
                    return;
                }

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Sélectionner un dessin à charger");

                ListView<String> listView = new ListView<>();
                drawingsMap.values().forEach(name -> listView.getItems().add(name));

                Button loadSelectedBtn = new Button("Charger");
                loadSelectedBtn.setDisable(true);

                listView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) ->
                        loadSelectedBtn.setDisable(newV.intValue() < 0));

                loadSelectedBtn.setOnAction(ev -> {
                    int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                    if (selectedIndex >= 0) {
                        Integer selectedId = (Integer) drawingsMap.keySet().toArray()[selectedIndex];
                        try {
                            drawingPane.getChildren().clear();
                            commandHistory.clear();
                            undoBtn.setDisable(true);
                            drawing = drawingDAO.loadDrawing(selectedId);
                            for (ShapeModel sm : drawing.getShapes()) {
                                drawingPane.getChildren().add(sm.getShape());
                            }
                            loggerContext.log("Dessin '" + drawing.getName() + "' chargé.");
                            showAlert("Succès", "Dessin chargé : " + drawing.getName());
                            dialogStage.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showAlert("Erreur", "Échec du chargement : " + ex.getMessage());
                        }
                    }
                });

                VBox vbox = new VBox(10, listView, loadSelectedBtn);
                vbox.setPadding(new Insets(10));
                Scene scene = new Scene(vbox, 300, 400);
                dialogStage.setScene(scene);
                dialogStage.show();

            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la récupération des dessins : " + ex.getMessage());
            }
        });

        clearBtn.setOnAction(e -> {
            drawing.clear();
            drawingPane.getChildren().clear();
            commandHistory.clear();
            undoBtn.setDisable(true);
            loggerContext.log("Toutes les formes ont été supprimées");
        });
    }

    private void onMousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    private void onMouseReleased(MouseEvent e) {
        double endX = e.getX();
        double endY = e.getY();

        ShapeModel shape = ShapeFactory.createShape(currentShapeType, startX, startY, endX, endY);

        // Command pattern: wrap the action and execute it
        DrawCommand cmd = new AddShapeCommand(shape, drawing, drawingPane);
        cmd.execute();
        commandHistory.push(cmd);
        undoBtn.setDisable(false);

        loggerContext.log("Forme dessinée: " + currentShapeType +
                " de (" + startX + "," + startY + ") à (" + endX + "," + endY + ")");

        if (loggerContext.getStrategy() instanceof FileLogger) {
            showLogFile();
        }
    }

    public BorderPane getView() {
        return root;
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void showLogFile() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("app.log"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("Erreur lors de la lecture du fichier log : ").append(e.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contenu du fichier de log");
        alert.setHeaderText("Fichier app.log");

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}