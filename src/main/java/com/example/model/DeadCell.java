package com.example.model;
/**
 * Cette classe represente une cellule morte dans la grid de temperature, elle hérite de cellule et ajt des fonctionnalités propre aux cellules mortes
 */
public class DeadCell extends Cell{
    public DeadCell(int row, int col, double temp) {
        super(row, col, 0);
    }

    //Une cellule morte n’a pas de température et ne diffuse pas la chaleur.
    // Une cellule morte ne rentre pas en compte dans le calcul de la température moyenne du système.
}
