package com.example.view;

import com.example.controller.SystemController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class StartingInterfaceView {

    private TextField deadCellsProbabilityField;
    private TextField systemSizeField;
    private TextField heatSourcesField;
    private TextField initialTempField;
    private ToggleGroup configGroup;
    private Button startButton;
    private SystemController systemController;

    // methode pour injecter le SystemController
    public void setSystemController(SystemController systemController) {
        this.systemController = systemController;
    }

    // creation et configuration du GridPane de l'interface de démarrage
    public GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        // taille du système de chauffe/de la grid
        Label systemSizeLabel = new Label("Taille du système (minimum 3):");
        grid.add(systemSizeLabel, 0, 0);
        systemSizeField = new TextField();
        grid.add(systemSizeField, 1, 0);

        // ajt des sources de chaleurs
        Label heatSourcesLabel = new Label("Nombre de sources de chaleurs (entre 4 et 9):");
        grid.add(heatSourcesLabel, 0, 1);
        heatSourcesField = new TextField();
        grid.add(heatSourcesField, 1, 1);

        // ajt de la température initial des sources de chaleur
        Label initialTempLabel = new Label("Température initiale des sources de chaleur (entre -50 et 100):");
        grid.add(initialTempLabel, 0, 2);
        initialTempField = new TextField();
        grid.add(initialTempField, 1, 2);

        // choix de la config des sources selon la configuration x ou +
        Label configurationLabel = new Label("Configuration des sources:");
        grid.add(configurationLabel, 0, 3);
        configGroup = new ToggleGroup();
        RadioButton configX = new RadioButton("x");
        configX.setToggleGroup(configGroup);
        RadioButton configPlus = new RadioButton("+");
        configPlus.setToggleGroup(configGroup);

        grid.add(configX, 1, 3);
        grid.add(configPlus, 1, 4);

        // probabilite des cellules mortes
        Label deadCellsProbabilityLabel = new Label("Probabilité cellules mortes (entre 0 et 1):");
        grid.add(deadCellsProbabilityLabel, 0, 6);
        deadCellsProbabilityField = new TextField();
        grid.add(deadCellsProbabilityField, 1, 6);

        startButton = new Button("Lancer la simulation");
        startButton.setDisable(true); // desac par défaut
        grid.add(startButton, 1, 7);

        // ajt des listeners pour valider les entrées des users
        addListeners();

        return grid;
    }

    // methode pour ajouter des listeners aux champs de texte et au bouton
    private void addListeners() {
        systemSizeField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        heatSourcesField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        initialTempField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        configGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        deadCellsProbabilityField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());

        // ajt d'un TextFormatter pour deadCellsProbabilityField pour permettre les valeurs décimales sinon ca acceptait que 0 ou 1 et rien d'autre
        Pattern validDoubleText = Pattern.compile("-?\\d*(\\.\\d*)?");
        TextFormatter<Double> textFormatter = new TextFormatter<>(
                new UnaryOperator<TextFormatter.Change>() {
                    @Override
                    public TextFormatter.Change apply(TextFormatter.Change change) {
                        String newText = change.getControlNewText();
                        if (validDoubleText.matcher(newText).matches()) {
                            return change;
                        }
                        return null;
                    }
                }
        );
        deadCellsProbabilityField.setTextFormatter(textFormatter);

        // apl au contrôleur lorsque l'utilisateur clique sur le bouton de lancement de simulation du systeme
        startButton.setOnAction(event -> {
            if (systemController != null) {  // verifier que le controller n'est pas null pour eviter des erreurs
                Stage stage = (Stage) startButton.getScene().getWindow();
                systemController.startSimulation(
                        systemSizeField.getText(),
                        heatSourcesField.getText(),
                        initialTempField.getText(),
                        ((RadioButton) configGroup.getSelectedToggle()).getText(),
                        deadCellsProbabilityField.getText(),
                        stage
                );
            } else {
                System.err.println("SystemController n'est pas initialisé!");
            }
        });
    }

    // methode pour valider les entrées des users
    private void validateInputs() {
        try {
            int systemSize = Integer.parseInt(systemSizeField.getText());
            int heatSources = Integer.parseInt(heatSourcesField.getText());
            double initialTemp = Double.parseDouble(initialTempField.getText());
            double deadCellsProbability = Double.parseDouble(deadCellsProbabilityField.getText());
            int minSystemSize = 3;
            int maxHeatSources = 9;
            int minHeatSources = 4;
            int maxInitialTemp = 100;
            int minInitialTemp = -50;
            int maxDeadCellsProbability = 1;
            int minDeadCellsProbability = 0;

            boolean configSelected = configGroup.getSelectedToggle() != null;
            boolean inputsValid = systemSize >= minSystemSize && heatSources >= minHeatSources && heatSources <= maxHeatSources && initialTemp >= minInitialTemp && initialTemp <= maxInitialTemp && deadCellsProbability >= minDeadCellsProbability && deadCellsProbability <= maxDeadCellsProbability;

            startButton.setDisable(!(configSelected && inputsValid));
        } catch (NumberFormatException e) {
            startButton.setDisable(true);
        }
    }
}
