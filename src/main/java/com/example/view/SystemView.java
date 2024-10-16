    package com.example.view;

    import com.example.controller.GridController;
    import com.example.controller.SystemController;
    import com.example.model.*;
    import com.example.observer.Observer;
    import com.example.simulation.TemperatureSimulationManager;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.control.ComboBox;
    import javafx.scene.control.Label;
    import javafx.scene.control.Slider;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.layout.VBox;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Rectangle;
    import javafx.stage.Stage;

    import java.util.List;


    public class SystemView implements Observer { // c'est l'observer car systemview observe la grid (model)  pour etre notifier des changements d'états

        private SystemController systemController; // besoin de faire ca ici car c'est le controller qui va gerer les actions de l'user comme les cliques de boutons et on va pas gerer ca dans la vue mais dans le controller
        private GridController gridController;
        private Grid grid;
        private GridPane gridPane;
        private BorderPane root;
        private Label timeLabel, extTempLabel, avgTempLabel;
        private VBox leftControls;
        private VBox rightControls;
        private Button playButton, pauseButton, resetButton;
        private ComboBox<String> heatModeSelector;
        private Slider speedSlider;
        private Stage systemStage;
        private TemperatureSimulationManager simulationManager;


        public SystemView(Grid grid, SystemController systemController, Stage systemStage) {
            this.grid = grid;
            this.systemController = systemController;
            this.systemStage = systemStage;
            grid.addObserver(this);
            initSystemView();
        }

        // ici on initialise la vue, cette methode est public pcq je l'appelle aussi dans la fenetre de demarrage
        public void initSystemView() {
            systemController.initializeSimulation(); //initialiser la simulation
            simulationManager = systemController.getSimulationManager(); //recupereration du manager de simulation
            gridController = new GridController(grid, simulationManager); //initialisation du controller de la grid

            gridPane = createGridPane();

            // BorderPane pour bien positionner la grid
            root = new BorderPane();
            root.setCenter(gridPane);
            root.setPadding(new Insets(10, 10, 10, 10));

            // initialiser  les contrôles
            initializeControls();

            // VBox pour les controles à gauche de la grid
            leftControls = createLeftControls();

            // ajt le VBox au panneau principal
            root.setLeft(leftControls);

            // ajt les controles à droite
            VBox rightControls = createRightControls();
            root.setRight(rightControls);

            //set up la scene
            Scene scene = new Scene(root, 1000, 900);
            systemStage.setScene(scene);
            systemStage.setTitle("System View");
            systemStage.show();
        }

        // ici on crée la grid de cellules
        private GridPane createGridPane() {
            GridPane gridPane = new GridPane();
            double cellSize = 600.0 / grid.getSize();

            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10, 10, 10, 10));

            int sourceCounter = 1; // compteur pour les sources de chaleur

            for (int row = 0; row < grid.getSize(); row++) {
                for (int col = 0; col < grid.getSize(); col++) {
                    StackPane stackPane = new StackPane();
                    Rectangle cellRect = new Rectangle(cellSize, cellSize);
                    cellRect.setStroke(Color.BLACK);
                    Cell cell = grid.getCell(row, col);
                    Label tempLabel = new Label();

                    if (cell instanceof DeadCell) {
                        cellRect.setFill(Color.BLACK);
                        tempLabel.setText(""); // pas de texte pour les cellules mortes
                    } else if (cell instanceof HeatSourceCell) {
                        HeatSourceCell heatSource = (HeatSourceCell) cell;
                        if (heatSource.isActive()) {
                            cellRect.setFill(grid.mapTemperatureToColor(heatSource.getTemp()));
                        } else {
                            cellRect.setFill(Color.GRAY); // grisé la cellule si elle est inactive
                        }
                        tempLabel.setTextFill(Color.BLACK);
                        tempLabel.setText("s" + sourceCounter);
                        sourceCounter++;
                    } else {
                        cellRect.setFill(grid.mapTemperatureToColor(cell.getTemp()));
                        tempLabel.setTextFill(Color.BLACK);  // txt noir pour être visible sur blanc
                        tempLabel.setText(String.format("%.1f°C", cell.getTemp()));
                    }
                    // centrer le label dans la cellule
                    StackPane.setAlignment(tempLabel, Pos.CENTER);
                    stackPane.getChildren().addAll(cellRect, tempLabel);

                    gridPane.add(stackPane, col, row);

                    // pour ouvrir la fenêtre de config de la cellule => délégation de la responsabilité au contrôleur pas de logique ici
                    int finalRow = row;
                    int finalCol = col;
                    stackPane.setOnMouseClicked(event -> gridController.onCellMenuClick(finalRow, finalCol));
                }
            }
            return gridPane;
        }

        // ici on initialise tout ce qui est boutons/labels/slider se situant sur la gauche de notre interface de controle
        private void initializeControls() {
            // les boutons de contrôle de l'user
            playButton = new Button("Play");
            pauseButton = new Button("Pause");
            resetButton = new Button("Reset");

            // J'attache des event handlers à chaque bouton et je delegue la responsabilite au controler pour ne pas mettre de logique dans cette classe
            //la logique métier est séparé de l'interface utilisateur de cette facon la
            playButton.setOnAction(event -> systemController.onPlayButton());
            pauseButton.setOnAction(event -> systemController.onPauseButton());
            resetButton.setOnAction(event -> systemController.onResetButton());

            // combobox pour choisir le mode de chauffe + delegation de la responsabilite au controler
            heatModeSelector = new ComboBox<>();
            heatModeSelector.getItems().addAll("Manuel", "Total", "Pair", "Impair", "Premier");
            heatModeSelector.setValue("Manuel");
            heatModeSelector.setOnAction(event -> systemController.onModeChange(heatModeSelector));

            // Slider pour un contrôle de vitesse de rafraichissement
            // Slider pour un contrôle de vitesse de rafraichissement
            speedSlider = new Slider();
            speedSlider.setMin(1); // vitesse ralentie
            speedSlider.setMax(3); // vitesse accélérée
            speedSlider.setValue(2); // par défaut, vitesse normale
            speedSlider.setShowTickLabels(true);
            speedSlider.setShowTickMarks(true);
            speedSlider.setMajorTickUnit(1);
            speedSlider.setMinorTickCount(0);
            speedSlider.setSnapToTicks(true); // le slider s'arrête sur les positions définies
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> systemController.onSliderChange(speedSlider));


            // Labels pour afficher les informations liée au temps de simulation, temperature exterieur ainsi que la temp moyenne
            timeLabel = new Label("Temps: ");
            extTempLabel = new Label("T° ext: ");
            avgTempLabel = new Label("T° moy: ");
        }

        // creation du conteneur pour les boutons de controle pour les positionner correctement ds l'interface de controle a gauche
        private VBox createLeftControls() {
            VBox leftControls = new VBox(10);
            leftControls.setPadding(new Insets(5));
            leftControls.getChildren().addAll(playButton, pauseButton, resetButton, heatModeSelector, speedSlider, timeLabel, extTempLabel, avgTempLabel);
            return leftControls;
        }

        // creation du conteneur pour les boutons de controle pour les positionner correctement ds l'interface de controle a droite
        private VBox createRightControls() {
            rightControls = new VBox(10);
            rightControls.setPadding(new Insets(5));

            updateHeatSourceButtons();
            updateRightControls();
            return rightControls;
        }

        // methode pour mettre a jour les boutons des sources de chaleurs
        private void updateHeatSourceButtons() {
            rightControls.getChildren().clear(); // Vider les anciens éléments

            List<HeatSourceCell> heatSources = grid.getHeatSources();
            for (int i = 0; i < heatSources.size(); i++) {
                HeatSourceCell source = heatSources.get(i);

                // creer un StackPane contenant un rectangle et un label
                Rectangle sourceRect = new Rectangle(100, 30);
                Label sourceLabel = new Label("Source " + (i + 1) + (source.isActive() ? " (Active)" : " (Inactive)"));

                // obtenir la couleur de la cellule dans la grid
                Color cellColor = grid.mapTemperatureToColor(source.getTemp());

                // si la source est inactive, on grise la couleur
                if (!source.isActive()) {
                    cellColor = Color.GRAY;
                }

                // appliquer la couleur au rectangle
                sourceRect.setFill(cellColor);

                // stackPane pour combiner le rectangle et le label
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(sourceRect, sourceLabel);
                StackPane.setAlignment(sourceLabel, Pos.CENTER);

                int sourceIndex = i;
                stackPane.setOnMouseClicked(event -> {
                    systemController.toggleHeatSource(sourceIndex); // Basculer l'état de la source
                    updateHeatSourceButtons(); // Mettre à jour après le changement
                });

                rightControls.getChildren().add(stackPane);
            }
        }


        // methode pour mettre à jour le label de la température moyenne
        public void updateAvgTempLabel(double avgTemp) {
            avgTempLabel.setText(String.format("T° moy: %.2f°C", avgTemp)); // Formater la temperature moyenne pour pas avoir trop de chiffres apres la virgule
        }

        // methode pour mettre à jour le label de la température extérieure
        public void updateExtTempLabel(double extTemp) {
            extTempLabel.setText("T° ext: " + extTemp + "°C");
        }

        // methode pour mettre à jour le label du temps
        public void updateTimeLabel(String time) {
            timeLabel.setText("Time: " + time);
        }

        @Override
        public void update() {
            System.out.println("Update method called in SystemView");
            gridPane.getChildren().clear();
            gridPane.getChildren().addAll(createGridPane().getChildren());

            updateRightControls();  // s'assurer que les controles sont update de maniere dynamique
            int elapsedTimeSeconds = systemController.getSimulationManager().getElapsedTime();

            // mettre à jour le label du temps avec le temps écoulé
            updateTimeLabel(elapsedTimeSeconds + " s");
            // mettre à jour la température extérieure
            double extTemp = simulationManager.getExternalTemperature();
            updateExtTempLabel(extTemp);
            // calcul et màj de la température moyenne
            double avgTemp = grid.calculateAverageTemperature();
            updateAvgTempLabel(avgTemp);
        }

        private void updateRightControls() {
            rightControls.getChildren().clear(); // Vider les anciens éléments

            List<HeatSourceCell> heatSources = grid.getHeatSources();
            for (int i = 0; i < heatSources.size(); i++) {
                HeatSourceCell source = heatSources.get(i);

                // Créer un StackPane contenant un rectangle et un label
                Rectangle sourceRect = new Rectangle(100, 30);
                Label sourceLabel = new Label("Source " + (i + 1) + (source.isActive() ? " (Active)" : " (Inactive)"));

                // obtenir la couleur de la cellule dans la grid
                Color cellColor = grid.mapTemperatureToColor(source.getTemp());

                if (!source.isActive()) {
                    cellColor = Color.GRAY;
                }

                // appliquer la couleur au rectangle
                sourceRect.setFill(cellColor);

                // StackPane pour combiner le rectangle et le label
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(sourceRect, sourceLabel);
                StackPane.setAlignment(sourceLabel, Pos.CENTER);

                int sourceIndex = i;
                stackPane.setOnMouseClicked(event -> {
                    systemController.toggleHeatSource(sourceIndex); // basculer l'état de la source
                    updateHeatSourceButtons(); // mettre à jour par apres le changement
                });

                rightControls.getChildren().add(stackPane);
            }
        }



    }
