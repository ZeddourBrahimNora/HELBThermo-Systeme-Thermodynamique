package com.example;

import com.example.controller.SystemController;
import com.example.model.Grid;
import com.example.view.StartingInterfaceView;
import com.example.simulation.SimulationDataParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Point d'entrée du programme
 */
public class Main extends Application {

    // static car je veut que cette variable soit accessible partout dans le programme
    public static String SIMULATION_FILE_PATH = "src/main/java/com/example/simul.data";

    @Override
    public void start(Stage primaryStage) {
        Grid grid = new Grid(10, 10, 10, "Random", 0.1);
        SystemController systemController = new SystemController(grid, null);
        // on va dabord afficher la fenetre de demarrage qui va permettre a l'user de configurer l'interface de controle selon ses besoins
        StartingInterfaceView startingInterfaceView = new StartingInterfaceView();

        // Assigner le contrôleur à la vue
        startingInterfaceView.setSystemController(systemController);
        
        Scene scene = new Scene(startingInterfaceView.createGridPane(), 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("HELBThermo simulation");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
