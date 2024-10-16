package com.example.controller;

import com.example.model.Grid;
import com.example.simulation.TemperatureSimulationManager;
import com.example.view.CellMenuView;

public class GridController {
    private Grid grid;
    private TemperatureSimulationManager simulationManager;

    public GridController(Grid grid, TemperatureSimulationManager simulationManager) {
        this.grid = grid;
        this.simulationManager = simulationManager;
    }

    //mettre en pause la simulation quand on clique sur le cellmenuview qui correspond a la fentre qui s'ouvre quand on clique sur une cellule dans la grid
    public void onCellMenuClick(int row, int col) {
        boolean wasRunning = simulationManager.isRunning();
        simulationManager.pauseSimulation();

        // Instanciation de la vue pour configurer la cellule
        CellMenuView cellMenuView = new CellMenuView(grid.getCell(row, col), grid);

        // Ajouter un événement pour détecter la fermeture de la fenêtre et redémarrer la simulation
        cellMenuView.getStage().setOnHiding(event -> {
            if (wasRunning) {
                simulationManager.startSimulation();
            }
        });

        // Affichage de la fenêtre
        cellMenuView.show();
    }

}
