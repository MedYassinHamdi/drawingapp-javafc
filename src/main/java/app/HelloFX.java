package app;

import controller.DrawingController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        DrawingController controller = new DrawingController();

        Scene scene = new Scene(controller.getView(), 800, 600);

        primaryStage.setTitle("Application de dessin géométrique");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
