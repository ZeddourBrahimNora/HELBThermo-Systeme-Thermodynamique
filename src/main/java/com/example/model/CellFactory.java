package com.example.model;

/*
    En gros ca rends le système bcp + facile a maintenir car on centralise la creation des objets grace a la fabrique
* */
public class CellFactory {

    // je met la methode en static car je veux pouvoir l'utiliser autre part sans devoir crée une instance de CellFactory, cette facon de faire est + adapté à mon cas
    public static Cell createCell(String cellType, int row, int col, double temp)
    {
        switch (cellType) {
            case "Regular":
                return new RegularCell(row, col, temp);
            case "Dead":
                return new DeadCell(row, col, 0); // 0 car pas de temp pour une cellule morte
            case "HeatSourceCell":
                return new HeatSourceCell(row, col, temp); // temperature défini par l'user au demarrage
            default:
                throw new IllegalArgumentException("Unknown cell type, it doesn't exist: " + cellType); // renvoyer une erreur si jamais on essaye de crée une cellule qui n'existe pas
        }
    }

}
