package com.example.simulation;

import com.example.Main;
import com.example.model.Cell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/* classe qui sert a parser les données de simulation 
* */
public class SimulationDataParser {
    private Queue<Double> temperatureData;
    private Cell cell;

    public SimulationDataParser(Cell cell) throws IOException {
        this.cell = cell;
        this.temperatureData = new LinkedList<>(); // LinkedList c une implémentation de Queue qui sert a stocker les températures
        parseFile();
    }

    // on utilise la méthode parseFile() pour lire le fichier de simulation et stocker les températures dans la liste temperatureData
    private void parseFile() throws IOException {
        boolean hasValidTemperature = false;
        int lineCount = 5; // nombre de caractere max qu'une ligne peut contenir

        try (BufferedReader reader = new BufferedReader(new FileReader(Main.SIMULATION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // ignorer les lignes vides
                if (line.isEmpty()) {
                    continue;
                }

                if (line.length() > lineCount) {
                    throw new IOException("Invalid line length: " + line);
                }

                try {
                    double temp = Double.parseDouble(line);
                    // on utilise les getters getMinTemp() et getMaxTemp() de la classe Cell pour obtenir les températures min et max
                    if (temp >= cell.getMinTemp() && temp <= cell.getMaxTemp()) {
                        temperatureData.add(temp);
                        hasValidTemperature = true;
                    } else {
                        System.out.println("Skipping invalid temperature: " + temp);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid temperature: " + line);
                }
            }
        }

        // verifier si aucune température valide n'a été trouvée
        if (!hasValidTemperature) {
            throw new IOException("No valid temperatures found in the file.");
        }
    }

    public Queue<Double> getTemperatureData() {
        return temperatureData;
    }
}
