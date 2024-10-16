package com.example.strategy;

import com.example.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/** Classe pour le mode de chauffe premier dans ce mode : chaque cellule qui n’est pas une source de
 chaleur initiale ou définie par l’utilisateur, et dont la somme des composantes
 de la position est un nombre premier, devient une source de chaleur, allumée à la
 température initiale définie dans l’interface de démarrage, pendant une
 seconde.
 * */
public class PremierHeatingModeStrategy  implements HeatingModeStrategy {
    @Override
    public void applyHeatMode(Grid grid, double externalTemperature) {
        System.out.println("Applying Premier Heating Mode.");

        // Listes pour stocker les informations nécessaires à la restauration
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        List<Double> temps = new ArrayList<>();
        List<String> types = new ArrayList<>();

        for (int row = 0; row < grid.getSize(); row++) {
            for (int col = 0; col < grid.getSize(); col++) {
                if (isPrime(row + col)) {
                    Cell currentCell = grid.getCell(row, col);
                    if (!(currentCell instanceof HeatSourceCell) && !(currentCell instanceof DeadCell)) {
                        // Stocker les informations de la cellule
                        rows.add(row);
                        cols.add(col);
                        temps.add(currentCell.getTemp());

                        if (currentCell instanceof RegularCell) {
                            types.add("Regular");
                        } else {
                            types.add("Unknown");
                        }

                        // Transformer la cellule en source de chaleur
                        grid.setCell(CellFactory.createCell("HeatSourceCell", row, col, grid.getInitialTemperature()));
                    }
                }
            }
        }

        grid.notifyObservers(); // informer les observateurs que du changement a eu lieu et qu'ils doivent se mettre à jour

        // creer un délai d'une seconde avant de restaurer les cellules
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            for (int i = 0; i < rows.size(); i++) {
                // restaurer l'état de la cellule
                grid.setCell(CellFactory.createCell(types.get(i), rows.get(i), cols.get(i), temps.get(i)));
            }
            grid.notifyObservers();
        }));

        timeline.setCycleCount(1);
        timeline.play();
    }

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

}
