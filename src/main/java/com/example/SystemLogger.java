package com.example;

import com.example.model.Cell;
import com.example.model.DeadCell;
import com.example.model.Grid;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton reponsable de la gestion des logs du systeme reprenant la journalisation des informations suivantes :
 * «nombre de secondes;T°moyenne;T°externe ;mode» et les températures de chaque cellule de la grid
 * La classe nomme le fichier log sous le format suivant : « DDMMYY_hhmmss.log »
 */
public class SystemLogger {

    private static SystemLogger instance;
    private FileWriter fileWriter;
    private boolean isClosed = false; // flag pour s'assurer que le flux est fermé pour eviter les erreurs

    //constructeur privé pour prevenir une instanciation externe de la classe
    private SystemLogger() {
    }

    // permet d'obtenir une instance unique de la classe logger
    public static synchronized SystemLogger getInstance() {
        if (instance == null) {
            instance = new SystemLogger();
        }
        return instance;
    }

    // initialise le filewriter pour creer un fichier log avec date + heure + secondes
    private void initializeFileWriter() {
        if (fileWriter == null && !isClosed) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy_HHmmss"));
                fileWriter = new FileWriter(timestamp + ".log", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // methode permettant de log un message
    public void log(String message) {
        initializeFileWriter(); // s'assurer que le fileWriter est bien initialisé
        if (fileWriter != null && !isClosed) {
            try {
                fileWriter.write(message + "\n");
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // reprend tt les informations dont on a besoin pour le contenu du fichier log
    public void logSystemState(int elapsedTimeSeconds, double averageTemperature, double externalTemperature, String mode, Grid grid) {
        // formatter les valeurs avec deux chiffres après la virgule
        String formattedMessage = String.format("%d;%.2f;%.2f;%s",
                elapsedTimeSeconds,
                averageTemperature,
                externalTemperature,
                mode);
        log(formattedMessage);

        // on recupere la taille de la grid pour pouvoir la parcourir en entier et on log les temperautres des cellules + "D" pour les cellules mortes
        for (int row = 0; row < grid.getSize(); row++) {
            StringBuilder rowLog = new StringBuilder();
            for (int col = 0; col < grid.getSize(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell instanceof DeadCell) {
                    rowLog.append("D;");
                } else {
                    // arrondir la température a deux chiffres après la virgule sinon c'était trop long
                    rowLog.append(String.format("%.2f", cell.getTemp())).append(";");
                }
            }
            log(rowLog.toString());
        }
    }

    // ne pas oublier de fermer le filewriter
    public void close() {
        if (fileWriter != null && !isClosed) {
            try {
                fileWriter.close();
                isClosed = true; // Marquer le fileWriter comme fermé
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
