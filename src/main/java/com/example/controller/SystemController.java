package com.example.controller;

import com.example.model.*;
import com.example.simulation.SimulationDataParser;
import com.example.simulation.TemperatureSimulationManager;
import com.example.strategy.*;
import com.example.view.SystemView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

/**
 * Controlleur de l'application dont les roles principaux sont:
 * -L'initialisation des paramètres globaux du système de chauffe
 * -L'association des events de l'UI aux actions appropriées
 * -Le lancement de la simulation via un Timer (Timeline)
 */
public class SystemController {

    private Grid grid;
    private SystemView systemView;
    private TemperatureSimulationManager simulationManager;
    private SimulationDataParser dataReader;
    private HeatingModeStrategy heatMode;

    public SystemController(Grid grid, TemperatureSimulationManager simulationManager) {
        this.grid = grid;
        this.simulationManager = simulationManager;
    }

    public void setSystemView(SystemView systemView) {
        this.systemView = systemView;
    }

    public void initializeSimulation() {
        try {
            dataReader = new SimulationDataParser(
                    CellFactory.createCell("Regular", 0, 0, 0)
            );
            simulationManager = new TemperatureSimulationManager(grid, dataReader);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de simulation : " + e.getMessage());
        }
    }

    public void startSimulation(String systemSizeText, String heatSourcesText, String initialTempText, String configurationText, String deadCellsProbabilityText, Stage currentStage) {
        currentStage.close();

        try {
            int systemSize = Integer.parseInt(systemSizeText);
            int heatSources = Integer.parseInt(heatSourcesText);
            double initialTemp = Double.parseDouble(initialTempText);
            double deadCellsProbability = Double.parseDouble(deadCellsProbabilityText);

            // Créer une instance de Grid avec les paramètres adéquats
            Grid grid = new Grid(systemSize, heatSources, initialTemp, configurationText, deadCellsProbability);

            // Créer une instance de SimulationDataParser avec une regular cell comme modele pour valider les données de simulation et s'assurer que les temp du fichier de simulation sont bien interprétées par le système
            SimulationDataParser dataParser = new SimulationDataParser(
                    CellFactory.createCell("Regular", 0, 0, initialTemp)
            );

            // Créer le TemperatureSimulationManager
            TemperatureSimulationManager simulationManager = new TemperatureSimulationManager(grid, dataParser);

            // Créer le SystemController avec grid et simulationManager
            SystemController systemController = new SystemController(grid, simulationManager);

            double externalTemperature = simulationManager.getExternalTemperature();
            grid.updateGrid(externalTemperature);

            // Créer et afficher la SystemView
            Stage simulationStage = new Stage(); // Nouvelle scène pour la simulation
            SystemView systemView = new SystemView(grid, systemController, simulationStage);
            systemController.setSystemView(systemView); // Ajout de cette ligne pour associer le SystemView au SystemController
            systemView.initSystemView();

            // Gérer la fermeture de l'application pour enregistrer l'état du système
            simulationStage.setOnCloseRequest(event -> {
                String mode = systemController.getCurrentHeatingMode();
                simulationManager.shutdown(mode); // Log et fermeture propre
            });

        } catch (NumberFormatException e) {
            System.out.println("Erreur: Veuillez entrer des valeurs numériques valides.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier de simulation : " + e.getMessage());
        }
    }


    public TemperatureSimulationManager getSimulationManager() { // mettre ca ici permet à la vue de demander indirectement des informations au modèle par l'intermédiaire du contrôleur car la vue ne peut pas accéder directement au modèle
        return simulationManager;
    }

    public void toggleHeatSource(int sourceIndex) {
        List<HeatSourceCell> heatSources = grid.getHeatSources();
        if (sourceIndex >= 0 && sourceIndex < heatSources.size()) {
            HeatSourceCell source = heatSources.get(sourceIndex);

            // Récupération des cellules adjacentes
            Cell[] adjacentCells = grid.getAdjacentCells(source.getRow(), source.getCol());

            // Obtenir la température extérieure depuis le simulation manager
            double externalTemp = simulationManager.getCurrentExternalTemperature();

            // Toggle active state et mise à jour de la température en fonction de l'état
            source.toggleActive(adjacentCells, externalTemp);

            grid.setCell(source);  // Mettre à jour la grille et notifier les observateurs
        }

    }

    public void setHeatMode(HeatingModeStrategy heatMode) {
        this.heatMode = heatMode;
    }

    public void onModeChange(ComboBox<String> modeSelector) {
        String selectedMode = modeSelector.getValue();
        HeatingModeStrategy strategy;

        switch (selectedMode) {
            case "Total":
                strategy = new TotalHeatingModeStrategy();
                break;
            case "Pair":
                strategy = new EvenHeatingModeStrategy();
                break;
            case "Impair":
                strategy = new OddHeatingModeStrategy();
                break;
            case "Premier":
                strategy = new PremierHeatingModeStrategy();
                break;
            default:
                strategy = new ManualHeatingModeStrategy();
        }

        setHeatMode(strategy);

        if (grid != null) {
            grid.setHeatingMode(strategy);
            strategy.applyHeatMode(grid, simulationManager.getCurrentExternalTemperature()); // on applique la stratégie directement
        } else {
            System.err.println("Grid is null when setting the heating mode.");
        }
    }

    public String getCurrentHeatingMode() {
        if (heatMode != null) {
            return heatMode.getClass().getSimpleName();
        }
        return "Manual"; // appeler "Manual" comme mode par défaut
    }


    // Gérer les changements de la valeur du slider de vitesse
    public void onSliderChange(Slider slider) {
        double newValue = slider.getValue();
        if (simulationManager != null) {
            if (newValue == 1) {
                simulationManager.setSimulationSpeed(0.5);  // Vitesse ralentie: toutes les 2 secondes
            } else if (newValue == 2) {
                simulationManager.setSimulationSpeed(1);  // Vitesse normale: toutes les 1 seconde
            } else if (newValue == 3) {
                simulationManager.setSimulationSpeed(2);  // Vitesse accélérée: toutes les 0,5 seconde
            }
        }
    }


    // Démarrer la simulation
    public void onPlayButton() {
        if (simulationManager != null) {
            simulationManager.startSimulation();
        }
    }

    // Mettre en pause la simulation
    public void onPauseButton() {
        if (simulationManager != null) {
            simulationManager.pauseSimulation();
        }
    }

    // Réinitialiser la simulation et la grille
    public void onResetButton() {
        if (simulationManager == null) {
            System.err.println("Error: simulationManager is not initialized.");
            return;
        }

        if (grid == null) {
            System.err.println("Error: grid is not initialized.");
            return;
        }

        if (systemView == null) {
            System.err.println("Error: systemView is not initialized.");
            return;
        }

        System.out.println("Resetting simulation...");
        simulationManager.resetSimulation();
        systemView.update();
        System.out.println("Simulation reset completed.");
    }



}
