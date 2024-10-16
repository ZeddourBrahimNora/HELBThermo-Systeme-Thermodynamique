package com.example.model;

public class HeatSourceCell extends Cell {

    private double activeTemperature; // la temperature de la cellule lorsqu'elle est active
    private boolean isActive;

    public HeatSourceCell(int row, int col, double initialTemperature) {
        super(row, col, initialTemperature);
        this.activeTemperature = initialTemperature;
        this.isActive = true;
    }
    public boolean isActive() {
        return isActive;
    }

    //utile lorsqu'on veut simplement inverser l'état actuel comme quand l'user clique sur la cellule pr activer ou desactiver manuelleemnt la cellule avec les boutons
    public void toggleActive(Cell[] adjacentCells, double externalTemp) {
        isActive = !isActive;
        updateTemperature(adjacentCells, externalTemp);
    }

    // la methode ici sert a mettre a jour la temperature de la cellule en fonction de son etat actif ou non
    public void updateTemperature(Cell[] adjacentCells, double externalTemp) {
        if (isActive) {
            // maintenir la température active si la source de chaleur est activée + s'assurer que c bien dans la plage de température qu'on a determiner
            setTemp(activeTemperature);
        } else {
            // se comporte comme une cellule normale influencée par les voisins et la temp extérieur
            if (adjacentCells != null) {
                double sumTemp = externalTemp;
                int count = 1;

                for (Cell adjacentCell : adjacentCells) {
                    if (!(adjacentCell instanceof DeadCell)) {
                        sumTemp += adjacentCell.getTemp();
                        count++;
                    }
                }
                setTemp(sumTemp / count);
            }
        }
    }

}
