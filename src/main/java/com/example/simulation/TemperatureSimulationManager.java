package com.example.simulation;

import com.example.SystemLogger;
import com.example.model.Grid;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Queue;

/*
* Classe qui sert a gerer la simulation de la température du fichier de simulation
* */
public class TemperatureSimulationManager {
    private Grid grid;
    private Queue<Double> temperatureData;
    private Timeline simulationTimeline;
    private int simulationSpeedMilliseconds = 1000; // 1 seconde par étape par défaut
    private double externalTemperature;
    private int elapsedTimeSeconds;
    private double firstValidTemperature; // la première température valide = la température initiale
    private SystemLogger logger;
    private long lastUpdateTime; // pour suivre le temps écoulé depuis la dernière mise à jour
    private long baseTime; // pour stocker l'heure de démarrage de la simulation

    public TemperatureSimulationManager(Grid grid, SimulationDataParser dataParser) {
        this.grid = grid;
        this.temperatureData = dataParser.getTemperatureData();
        this.firstValidTemperature = getNextTemperature();  // recupere la première température valide
        this.externalTemperature = firstValidTemperature; // initialise la température externe avec la première température valide
        this.grid.setInitialTemperature(firstValidTemperature); // definit la temperature initiales de tt les cellules pour qu'elles commencent avec cette valeur de base la
        this.logger = SystemLogger.getInstance(); // initialiser le logger grace a getInstance car il s'agit d'un singleton
        this.baseTime = System.currentTimeMillis(); // initialiser avec le temps actuel
        this.lastUpdateTime = baseTime; // initialiser le dernier temps de mise à jour
        initializeSimulation();
    }

    // log l'état actuel du système
    public void logSystemState(String mode) {
        double averageTemperature = grid.calculateAverageTemperature();
        logger.logSystemState(elapsedTimeSeconds, averageTemperature, externalTemperature, mode, grid);
    }

    //arrete la simulation en enregistrant l'état final du systeme avec les dernieres valeurs valides
    public void shutdown(String mode) {

        long currentTime = System.currentTimeMillis();
        elapsedTimeSeconds = (int) ((currentTime - baseTime) / 1000);
        externalTemperature = getCurrentExternalTemperature();

        // Log l'état final avec les dernières valeurs valides
        logSystemState(mode);
        logger.close(); // Fermer le logger
    }

    //initialise la simulation
    private void initializeSimulation() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.millis(simulationSpeedMilliseconds), event -> {
            long currentTime = System.currentTimeMillis();
            elapsedTimeSeconds = (int) ((currentTime - baseTime) / 1000); // Calcul du temps écoulé depuis le début

            externalTemperature = getNextTemperature();
            System.out.println("Time: " + elapsedTimeSeconds + "s, External Temperature: " + externalTemperature + "°C");

            grid.updateGrid(externalTemperature);

            lastUpdateTime = currentTime; // mettre à jour le temps de la dernière mise à jour


        }));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE); // permet  a la simulation de continuer de fonctionner sans interruptuion jusqu'à ce qu'elle soit stoppée
    }

    public void resetSimulation() {
        if (simulationTimeline == null || simulationTimeline.getStatus() == Timeline.Status.STOPPED) {
            System.err.println("Cannot reset: simulationTimeline is not initialized or already stopped.");
            return;
        }

        String timelineState = simulationTimeline.getStatus().toString(); // sauvegarder l'état actuel
        simulationTimeline.stop();  // arreter la simulation

        externalTemperature = firstValidTemperature; // reprendre la température initiale
        grid.resetGrid(firstValidTemperature);  // reinitialiser les cellules sauf sources de chaleur et cellules mortes

        baseTime = System.currentTimeMillis(); // reinitialiser le temps de base
        lastUpdateTime = baseTime;

        initializeSimulation();  // reinitialiser la timeline de simulation
        System.out.println("Simulation reset.");

        // retablir l'état de la timeline donc RUNNING ou PAUSED
        if (timelineState.equals("RUNNING")) {
            startSimulation();
        } else if (timelineState.equals("PAUSED") || timelineState.equals("STOPPED")) {
            pauseSimulation();
        }
    }

    //recupere la prochaine température de la queue
    private double getNextTemperature() {
        Double temperature = temperatureData.poll(); // recup et retirer la première température de la queue
        if (temperature == null) {  // si la queue est vide = fin du fichier de simulation
            temperature = temperatureData.peek(); // recommencer depuis le début et essayer de recup la première température à nouveau
            temperatureData.addAll(temperatureData); // reinserer toutes les températures à la fin de la queue pour les réutiliser une nvl fois
        }
        return temperature != null ? temperature : 0.0; // vérifie si temperature est pas nulle si elle est pas nulle renvoie la valeur de temp si elle est nulle  renvoie 0.0.
    }

    public void startSimulation() {
        if (simulationTimeline != null) {
            lastUpdateTime = System.currentTimeMillis(); // reinitialiser le temps depuis la dernière mise à jour, permet de suivre le temps écoulé depuis la derniere maj
            simulationTimeline.play();
            System.out.println("Simulation started.");
        }
    }
    // met en pause la simulation
    public void pauseSimulation() {
        if (simulationTimeline != null) {
            simulationTimeline.pause();
            System.out.println("Simulation paused.");
        }
    }

    public boolean isRunning() {
        return (simulationTimeline.getStatus() != Timeline.Status.STOPPED && simulationTimeline.getStatus() != Timeline.Status.PAUSED);
    }

    public int getElapsedTime() {
        return elapsedTimeSeconds;
    }

    public double getCurrentExternalTemperature() {
        return externalTemperature;
    }

    public double getExternalTemperature() {
        return this.externalTemperature;
    }

    // permet de changer la vitesse de la simulation
    public void setSimulationSpeed(double speedFactor) {
        // stopper la timeline si elle est en cours d'exécution
        if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();
        }

        // ajuster la vitesse en fonction des facteurs de vitesse definit dans l'énoncé
        if (speedFactor == 0.5) {
            this.simulationSpeedMilliseconds = 2000;  // vitesse ralentie
        } else if (speedFactor == 1) {
            this.simulationSpeedMilliseconds = 1000;  // vitesse normale
        } else if (speedFactor == 2) {
            this.simulationSpeedMilliseconds = 500;  // vitesse accélérée
        }

        // mettre à jour la timeline avec la nouvelle vitesse
        if (simulationTimeline != null) {
            simulationTimeline.getKeyFrames().clear();
            simulationTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(simulationSpeedMilliseconds), event -> {
                        long currentTime = System.currentTimeMillis();
                        elapsedTimeSeconds = (int) ((currentTime - baseTime) / 1000); // recalculer le temps écoulé depuis le début

                        externalTemperature = getNextTemperature(); // assigner la prochaine température externe
                        grid.updateGrid(externalTemperature);

                        lastUpdateTime = currentTime; // mettre à jour le temps de la dernière mise à jour
                    })
            );

            // redemarrer la timeline
            simulationTimeline.play();
        }
    }
}
