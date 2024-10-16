package com.example.model;

import com.example.observer.Observable;
import com.example.observer.Observer;
import com.example.strategy.HeatingModeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;


public class Grid implements Observable {
    private static final int COLOR_MAX_VALUE = 255; // 255 car c l'intensité max qu'on atteint dans le rgb et static final car ca va pas bouger
    private static final int COLOR_MIN_VALUE = 0; // 0 car c l'intensité min qu'on atteint dans le rgb et static final car ca va pas bouger
    private static final int FULL_SCALE = 1; // 1, c'est la valeur complète ca veut dire qu'on passe complètement d'une couleur à une autre  sans sauter/ignorer aucune partie de la transition
    private int size; // Taille de la grille LignesxColonnes choisit par l'user dans startingInterfaceView
    private int heatSources;
    private double initialTemp;
    private double deadCellsProbability;
    private Cell[][] cells;
    private String configuration;
    private List<Observer> observers;
    private HeatingModeStrategy heatingMode;


    public Grid(int size, int heatSources, double initialTemp, String configuration, double deadCellsProbability) {
        this.size = size;
        this.heatSources = heatSources;
        this.initialTemp = initialTemp;
        this.configuration = configuration;
        this.deadCellsProbability = deadCellsProbability;
        this.observers = new ArrayList<>();
        this.cells = new Cell[size][size];
        initGrid();
    }

    //on initialise la grid avec tt les bon éléments
    private void initGrid() {
        initRegularCells();
        placeHeatSources();
        assignDeadCells();
        notifyObservers();
    }

    // on initialise les cellules normales
    private void initRegularCells() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = CellFactory.createCell("Regular", row, col, initialTemp);
            }
        }
    }

    //pour réinitialiser la grid
    public void resetGrid(double initialTemperature) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = cells[row][col];
                if (!(cell instanceof DeadCell) && !(cell instanceof HeatSourceCell)) {
                    cell.setTemp(initialTemperature);  // Réinitialise les cellules normales avec la température initiale
                }
            }
        }
        notifyObservers();  // notifie les observateurs que la grid a été réinitialisée pour qu'ils puissent faire le rafraîchissement de l'interface
    }

    public void setHeatingMode(HeatingModeStrategy heatingMode) {
       this.heatingMode = heatingMode;
    }

    // initialiser la temperature initiale des cellules excepté pour les cellules mortes qui n'en ont pas
    public void setInitialTemperature(double temperature) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!(cells[row][col] instanceof DeadCell) && !(cells[row][col] instanceof HeatSourceCell)) {
                    cells[row][col].setTemp(temperature);
                }
            }
        }
    }

    // Méthode pour assigner les cellules mortes selon la probabilité définie par l'utilisateur
    private void assignDeadCells() {
        Random rand = new Random(); // genere un nbr random
        for (int row = 0; row < size; row++) { // parcourt tt les lignes de la grid
            for (int col = 0; col < size; col++) { // parcourt tt les colonnes de la grid
                // verifie si la cellule actuelle n'est pas déjà une source de chaleur
                if (!(cells[row][col] instanceof HeatSourceCell)) {
                    // genere un nombre random entre 0 et 1 et compare avec la probabilité des cellules mortes definie par l'utilisateur
                    if (rand.nextDouble() < deadCellsProbability) {
                        // attribuer la cellule comme etant une cellule morte si la condition est vraie
                        cells[row][col] = CellFactory.createCell("Dead", row, col, 0);
                    }
                }
            }
        }
    }

    // on place les sources de chaleur selon la config choisit par l'user donc x ou +
    private void placeHeatSources() {
        if (configuration.equals("+")) {
            placeHeatSourcesPlus();
        } else if (configuration.equals("x")) {
            placeHeatSourcesX();
        }
    }

   /* Dans la configuration « + », les sources de chaleurs sont placées sur la colonne
    (taille du système)/2 et sur la ligne (taille du système)/2 en commençant par remplir
    d’abord les cellules centrales, dans le sens horlogique*/
    private void placeHeatSourcesPlus() {
    int center = size / 2;
    int sourcesPlaced = 0;

    // Placer la cellule centrale en premier
    if (isValidPosition(center, center)) {
        placeHeatSource(center, center);
        sourcesPlaced++;
    }

    // Placer les autres sources de chaleur
    for (int i = 1; sourcesPlaced < heatSources && i <= center; i++) {
        if (sourcesPlaced < heatSources && isValidPosition(center - i, center)) { // verifie si la position au dessus du centre est valide et s'il reste des sources à placer
            placeHeatSource(center - i, center); // place la source de chaleur au dessus du centre
            sourcesPlaced++; // incrementer le nombre de sources placée
        }
        if (sourcesPlaced < heatSources && isValidPosition(center + i, center)) { // verifie si la position en dessous du centre est valide et s'il reste des sources à placer
            placeHeatSource(center + i, center); // place la source de chaleur en dessous du centre
            sourcesPlaced++;
        }
        if (sourcesPlaced < heatSources && isValidPosition(center, center - i)) { // verifie si la position à gauche du centre est valide et s'il reste des sources à placer
            placeHeatSource(center, center - i);// place la source de chaleur à gauche du centre
            sourcesPlaced++;
        }
        if (sourcesPlaced < heatSources && isValidPosition(center, center + i)) { // verifie si la position à droite du centre est valide et s'il reste des sources à placer
            placeHeatSource(center, center + i); // place la source de chaleur a droite du centre
            sourcesPlaced++;
        }
    }
}

/*Dans la configuration « x », les sources de chaleurs sont placées sur les
diagonales du système en commençant par remplir d’abord les cellules centrales,
dans le sens horlogique. */
// placement ne se faisant pas selon le pattern decrit dans l'énoncé, la logique n'est pas correcte
    private void placeHeatSourcesX() {
        // calcul du centre
        int centerRow = size / 2;
        int centerCol = size / 2;

        int sourcesPlaced = 0;

        // liste pour stocker les positions déjà occupées des sources de chaleur
        List<int[]> positions = new ArrayList<>();

        // on place la première source de chaleur au centre si la position est ok
        if (isValidPosition(centerRow, centerCol)) {
            placeHeatSource(centerRow, centerCol);
            positions.add(new int[]{centerRow, centerCol}); // ajt  la position centrale à la liste des positions oqp
            sourcesPlaced++; // on incremente le cpt de sources placées
        }

        // logique de placement des sources de chaleur
        while (sourcesPlaced < heatSources) { // ici on boucle jusqu'a ce que tt les sources soient placées
            List<int[]> nextPositions = new ArrayList<>(); // liste pour stocker les nvl positions à verifier

            for (int[] currentPos : positions) { // pour chaque pos actuelle
                int row = currentPos[0]; // recup la ligne  de la pos actuelle
                int col = currentPos[1]; // recup la colonne de la pos actuelle

                Cell[] adjacentCells = getAdjacentCells(row, col); // recup des cellules adjacentes
                for (Cell adjacent : adjacentCells) { // pr chaque cell adjacente
                    if (adjacent != null && !(adjacent instanceof HeatSourceCell) && sourcesPlaced < heatSources) { // si la cell est valide et n'est pas deja une source de chaleur
                        int newRow = adjacent.getRow();
                        int newCol = adjacent.getCol();

                        if (!containsPosition(positions, newRow, newCol)) { //si la pos n'est pas deja oqp
                            placeHeatSource(newRow, newCol); // placer la source de chaleur a la nvl position
                            nextPositions.add(new int[]{newRow, newCol}); // ajt de la nvl positiona  a la liste des positions a verifier
                            sourcesPlaced++;
                        }
                    }
                }
            }

            // ajt les nouvelles positions à la liste d positions oqp
            positions.addAll(nextPositions);
        }
    }

    // Méthode pour vérifier la validité de la position
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size; // Vérifie si la position est dans les limites de la grid
    }

    // Méthode pour placer une source de chaleur
    private void placeHeatSource(int row, int col) {
        cells[row][col] = CellFactory.createCell("HeatSourceCell", row, col, initialTemp); // crée une nvl cell de type HeatSourceCell à la position specifiee et l'ajoute à la grid
    }

    // Méthode pour vérifier si la position est déjà dans la liste
    private boolean containsPosition(List<int[]> positions, int row, int col) {
        for (int[] pos : positions) {
            if (pos[0] == row && pos[1] == col) { // Si la pos (row, col) est trouvée dans la liste retourne true
                return true;
            }
        }
        // si la pos est pas trouvée ds la liste on retourne false
        return false;
    }

    // methode pour recuperer les sources de chaleur de la grid
    public List<HeatSourceCell> getHeatSources() {
        List<HeatSourceCell> heatSources = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col] instanceof HeatSourceCell) {
                    heatSources.add((HeatSourceCell) cells[row][col]);
                }
            }
        }
        return heatSources;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public int getSize() {
        return size;
    }



    // responsable de la mise à jour des températures en fonction des differentes stratégies de chauffage
    public void updateGrid(double extTemp) {
        System.out.println("=== Start of updateGrid ===");
        System.out.println("External Temperature: " + extTemp);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell currentCell = cells[row][col];
                double oldTemp = currentCell.getTemp(); // Température avant mise à jour

                if (currentCell instanceof RegularCell) {
                    System.out.println("Updating regular cell at (" + row + ", " + col + ")");
                    currentCell.setTemp(calculateNewTemperature(extTemp, row, col));
                } else if (currentCell instanceof HeatSourceCell) {
                    HeatSourceCell heatSourceCell = (HeatSourceCell) currentCell;
                    if (heatSourceCell.isActive()) {
                        System.out.println("Active heat source cell at (" + row + ", " + col + ") retains its temperature: " + heatSourceCell.getTemp());
                    } else {
                        System.out.println("Updating inactive heat source cell at (" + row + ", " + col + ")");
                        heatSourceCell.setTemp(calculateNewTemperature(extTemp, row, col));
                    }
                }

                double newTemp = currentCell.getTemp(); // Température après mise à jour
                System.out.println("Cell (" + row + ", " + col + ") updated from " + oldTemp + "°C to " + newTemp + "°C");
            }
        }

        System.out.println("=== End of updateGrid ===");
        notifyObservers(); // ne pas oublier de notifier les observers
    }

    // Méthode pour récupérer la température initiale définie par l'utilisateur
    public double getInitialTemperature() {
        return initialTemp;
    }

    // methode pour calculer la température mise a jour
    private double calculateNewTemperature(double extTemp, int row, int col) {
        // On commence en initialisant sumTemp avec la température de la cellule elle mm ca va  inclure la température actuelle de la cellule dans le calcul de la moyenne
        double sumTemp = cells[row][col].getTemp();
        int count = 1; // ce compteur commence a 1 car on a deja ajt la temperature de la cellule elle meme

        // recup un tableau de toutes les cellules adjacentes valides => celles qui  sont pas hors limite ou deadcell
        Cell[] adjacentCells = getAdjacentCells(row, col);
        // on va parcourir chaque cellule adjacente pour ajt sa temperature à sumTemp
        for (Cell adjacentCell : adjacentCells) {
            // si la cellule adjacente n'est pas morte on l'ajt au calcul
            if (!(adjacentCell instanceof DeadCell)) {
                // on ajt la temperature de la cellule adjacente à sumTemp
                sumTemp += adjacentCell.getTemp();
                count++; // on incremente ici pcq  on a pris en compte une temperature supp
            }
        }

        // verifie si la cellule est située en bordure de la grid
        // une cellule est en bordure si elle est sur la première ligne donc row == 0 et la dernière ligne row == size - 1
        // la première colonne col == 0 ou la dernière colonne col == size - 1
        if (row == 0 || row == size - 1 || col == 0 || col == size - 1) {
            // si c'est le cas on ajoute la température extérieure à sumTemp
            sumTemp += extTemp;
            count++;
        }
        //  on retourne la moyenne des température
        return sumTemp / count;
    }

    // methode pour calculer la temperature moyenne du systeme
    public double calculateAverageTemperature() {
        double totalTemp = 0.0;
        int count = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = cells[row][col];
                if (!(cell instanceof DeadCell)) { // On exclut les cellules mortes
                    totalTemp += cell.getTemp();
                    count++;
                }
            }
        }

        if (count == 0) {
            return 0.0; // evite la division par zéro pcq si on a que des cellules mortes on aura rien a calculé car pas de cellules "vivantes"
        }

        return totalTemp / count;
    }

    // permet de modifier la cellule quand je la change dans cellmenuview  ( changement de type de cellules)
    public void setCell(Cell cell) {
        cells[cell.getRow()][cell.getCol()] = cell;  // mettre à jour la cellule dans la grille
        notifyObservers();  // Notifier les observateurs de la grid pour qu'ils se rafraîchissent
    }

    public Cell[] getAdjacentCells(int row, int col) {
        List<Cell> adjacentCells = new ArrayList<>();

        // Parcourt les cellules autour de la cellule donnée (haut, bas, gauche, droite, diagonales)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // on ignore la cellule centrale
                int newRow = row + i;
                int newCol = col + j;

                if (isValidPosition(newRow, newCol)) {
                    Cell adjacentCell = cells[newRow][newCol];
                    if (!(adjacentCell instanceof DeadCell)) { // on ajoute que les cellules qui ne sont pas mortes
                        adjacentCells.add(adjacentCell);
                    }
                }
            }
        }

        return adjacentCells.toArray(new Cell[0]); // Retourne un tableau des cellules adjacentes valides
    }

    public Color mapTemperatureToColor(double temperature) {
        // Toutes les cellules ont le même Tmin et Tmax définis dans Cell.java
        double Tmin = cells[0][0].getMinTemp();
        double Tmax = cells[0][0].getMaxTemp();

        if (temperature <= 0) {
            return getColorForNegativeTemp(temperature, Tmin); // Gérer les températures négatives
        } else {
            return getColorForPositiveTemp(temperature, Tmax); // Gérer les températures positives
        }
    }

    private Color getColorForNegativeTemp(double temperature, double Tmin) {
        // définition des couleur pour les températures négatives :
        Color darkBlue = Color.rgb(0, 0, 255); // bleu foncé
        Color mediumBlue = Color.rgb(127, 127, 255); // Bleu moyen
        Color lightBlue = Color.rgb(191, 191, 255); // Bleu clair
        Color white = Color.rgb(255, 255, 255); // Blanc

        if (temperature <= Tmin) {
            return darkBlue; // Retourner une couleur bleu foncé pour Tmin
        } else if (temperature <= Tmin / 2) {
            return interpolateColor(temperature, Tmin, Tmin / 2, darkBlue, mediumBlue);
            // Si la température est entre Tmin et Tmin/2, on passe graduellement du bleu foncé à une couleur intermédiaire (Tmin/2)
        } else if (temperature <= Tmin / 4) {
            return interpolateColor(temperature, Tmin / 2, Tmin / 4, mediumBlue, lightBlue);
            // Si la température est entre Tmin/2 et Tmin/4, on passe graduellement de cette couleur intermédiaire (Tmin/2) à une autre (Tmin/4)
        } else {
            return interpolateColor(temperature, Tmin / 4, 0, lightBlue, white);
            // Sinon, on passe graduellement de Tmin/4 vers 0 en allant du bleu clair au blanc
        }
    }

    private Color getColorForPositiveTemp(double temperature, double Tmax) {

        // Définition des couleurs pour les températures positives
        Color red = Color.rgb(255, 0, 0); // Rouge
        Color mediumRed = Color.rgb(255, 127, 127); // Rouge moyen
        Color lightRed = Color.rgb(255, 191, 191); // Rouge clair
        Color white = Color.rgb(255, 255, 255); // Blanc

        if (temperature >= Tmax) {
            return red; // Retourner une couleur rouge pour Tmax
        } else if (temperature >= Tmax / 2) {
            return interpolateColor(temperature, Tmax / 2, Tmax, mediumRed, red);
            // Si la température est entre Tmax/2 et Tmax, on passe graduellement d'une couleur intermédiaire (Tmax/2) au rouge
        } else if (temperature >= Tmax / 4) {
            return interpolateColor(temperature, Tmax / 4, Tmax / 2, lightRed, mediumRed);
            // Si la température est entre Tmax/4 et Tmax/2, on passe graduellement d'une couleur intermédiaire (Tmax/4) à Tmax/2
        } else {
            return interpolateColor(temperature, 0, Tmax / 4, white, lightRed);
            // Sinon, on passe graduellement de 0 vers Tmax/4, en allant du blanc à une couleur rose clair
        }
    }

    // methode qui va interpoler
    private Color interpolateColor(double temperature, double minTemp, double maxTemp, Color startingColor, Color endingColor) {
        // startingColor et endingColor sont les couleurs entre lesquelles on va interpoler pour avoir les resultats voulus
        // startingColor correspond à la couleur de la température minTemp et endingColor correspond à la couleur de la température maxTemp
        // Calcul du ratio de position de la température entre minTemp et maxTemp => cela va nous donner une valeur entre 0 et 1
        // qui servira à déterminer à quelle couleur la température correspond entre les deux extremes qu'on a
        double ratio = (temperature - minTemp) / (maxTemp - minTemp);

        int red = (int) (startingColor.getRed() * COLOR_MAX_VALUE * (FULL_SCALE - ratio) + endingColor.getRed() * COLOR_MAX_VALUE * ratio);
        int green = (int) (startingColor.getGreen() * COLOR_MAX_VALUE * (FULL_SCALE - ratio) + endingColor.getGreen() * COLOR_MAX_VALUE * ratio);
        int blue = (int) (startingColor.getBlue() * COLOR_MAX_VALUE * (FULL_SCALE - ratio) + endingColor.getBlue() * COLOR_MAX_VALUE * ratio);

        // s'assurer que les valeurs de couleur sont bien comprises entre 0 et 255 pour éviter des erreurs de couleur
        red = Math.min(COLOR_MAX_VALUE, Math.max(COLOR_MIN_VALUE, red));
        green = Math.min(COLOR_MAX_VALUE, Math.max(COLOR_MIN_VALUE, green));
        blue = Math.min(COLOR_MAX_VALUE, Math.max(COLOR_MIN_VALUE, blue));

        // Retourner la couleur interpolée
        return Color.rgb(red, green, blue);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
