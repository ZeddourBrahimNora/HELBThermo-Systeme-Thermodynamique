package com.example.model;
/**
 * Cette classe represente une cellule normal dans la grid de temperature, elle hérite de cellule et si des fonctionnalités propre aux cellules normales doivent être ajoutées elles seronr  ajoutées ici
 */

public class RegularCell extends Cell {
    public RegularCell(int row, int col, double temp) {
        super(row, col, temp);
    }


}
