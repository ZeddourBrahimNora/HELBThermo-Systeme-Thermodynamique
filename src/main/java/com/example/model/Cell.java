package com.example.model;

/**
 * Cette classe represente une cellule dans la grid du systeme de controle
 * La classe est abstraite car j'aimerai fournir un comportement general pour toutes les classes filles mais elles ne vont pas toutes implementer l'integralité
 * du code se trouvant dans la classe mere qui est donc la classe Cell
 */
public abstract class Cell {

    // pos de la cellule en row x col
    protected int row;
    protected int col;
    protected double temp;
    protected double minTemp = -50.0;
    protected double maxTemp = 100.0;

    public Cell(int row, int col, double temp){
        this.row = row;
        this.col = col;
        this.temp = temp;
    }
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // ici on va s'assurer que la temperature reste dans les limites qu'on a declaré au debut donc entre -50 et 100
    //Math.min renvoie la + petite valeur et Math.max renvoie la + grande valeur
    public void setTemp(double temp) {
        this.temp = Math.min(Math.max(temp, minTemp), maxTemp);
    }

    public double getTemp() {
        return temp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }



}
