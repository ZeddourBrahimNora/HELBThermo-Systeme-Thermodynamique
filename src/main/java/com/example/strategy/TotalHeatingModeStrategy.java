package com.example.strategy;

import com.example.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/** Classe pour le mode de chauffe total : : dans ce mode, chaque cellule qui n’est pas une source de
 chaleur initiale ou définie par l’utilisateur, devient une source de chaleur,
 allumée à la température initiale définie dans l’interface de démarrage,
 pendant une seconde.
 * */
public class TotalHeatingModeStrategy  implements HeatingModeStrategy  {
    @Override
    public void applyHeatMode(Grid grid, double externalTemperature) {
        System.out.println("Applying Total Heating Mode.");

        // listes pour stocker les informations nécessaires a la restauration des cellules dans la grid apres l'application d modes de chauffes
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        List<Double> temps = new ArrayList<>();
        List<String> types = new ArrayList<>();

        for (int row = 0; row < grid.getSize(); row++) {
            for (int col = 0; col < grid.getSize(); col++) {
                Cell currentCell = grid.getCell(row, col);
                if (!(currentCell instanceof HeatSourceCell) && !(currentCell instanceof DeadCell)) {
                    // stock les informations de la cellule
                    rows.add(row);
                    cols.add(col);
                    temps.add(currentCell.getTemp());

                    if (currentCell instanceof RegularCell) {  // si c'est une cellule normale
                        types.add("Regular");
                    } else {
                        types.add("Unknown");
                    }

                    // transformer la cellule en source de chaleur
                    grid.setCell(CellFactory.createCell("HeatSourceCell", row, col, grid.getInitialTemperature()));
                }
            }
        }
        grid.notifyObservers();
        // creer un délai d'une seconde avant de restaurer les cellules
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            for (int i = 0; i < rows.size(); i++) {
                // restaurer l'état de la cellule pr continuer la simulation normalement
                grid.setCell(CellFactory.createCell(types.get(i), rows.get(i), cols.get(i), temps.get(i)));
            }
            grid.notifyObservers();
        }));

        timeline.setCycleCount(1);
        timeline.play();
    }
}
