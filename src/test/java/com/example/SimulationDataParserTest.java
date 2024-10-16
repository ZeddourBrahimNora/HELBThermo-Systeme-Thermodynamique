package com.example;

import com.example.Main;
import com.example.model.Cell;
import com.example.model.RegularCell;
import com.example.simulation.SimulationDataParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class SimulationDataParserTest {

    private File tempFile;  // Fichier temporaire pour les tests
    private SimulationDataParser simulationDataParser;  // Instance de SimulationDataParser pour chaque test

    @BeforeEach
    public void setUp() throws Exception {
        // ici je cree un fichier temporaire pour chaque test
        tempFile = File.createTempFile("temp", ".txt");
        // je defini le chemin du fichier de simulation à utiliser pour les tests
        Main.SIMULATION_FILE_PATH = tempFile.getAbsolutePath();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // on supp le fichier temporaire apres chaque test
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testValidTemperaturesAreReadCorrectly() throws IOException {
        // on teste en ecrivant des temperatures valides dans le fichier temporaire
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("7.32\n");
            writer.write("20.5\n");
            writer.write("30.2\n");
        }

        // on initialise SimulationDataParser avec une cellule RegularCell pour valider les données de simulation
        simulationDataParser = new SimulationDataParser(new RegularCell(0, 0, 0));
        Queue<Double> temperatureData = simulationDataParser.getTemperatureData();

        // verifier que les températures valides ont bien été ajoutées assertEquals renverra une erreur si les valeurs ne sont pas égales
        assertEquals(3, temperatureData.size());
        assertEquals(7.32, temperatureData.poll(), 0.01);
        assertEquals(20.5, temperatureData.poll(), 0.01);
        assertEquals(30.2, temperatureData.poll(), 0.01);
    }

    @Test
    public void testFileWithEmptyLines() throws IOException {
        // Écrire des lignes vides et une température valide
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("\n"); // ligne vide
            writer.write("   \n"); // ligne avec des espaces
            writer.write("25.0\n");  // température valide
        }

        // Initialiser SimulationDataParser avec une cellule RegularCell pcq c une implementation de la classe abstract Cell qui n'est pas instanciable
        // on utilise regular cell ici pour s'assurer que les methodes getMinTemp() et getMaxTemp() retournent les valeurs correctes et que la validation des températures fonctionne bien dans le contexte de la simulation
        simulationDataParser = new SimulationDataParser(new RegularCell(0, 0, 0));
        Queue<Double> temperatureData = simulationDataParser.getTemperatureData();

        // verifier que seule la température valide a été ajoutée
        assertEquals(1, temperatureData.size());
        assertEquals(25.0, temperatureData.poll(), 0.01);
    }

    @Test
    public void testFileWithInvalidTemperaturesAreSkipped() throws IOException {
        // ecrire des températures valides et invalides dans le fichier temporaire
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("7.32\n"); // valide
            writer.write("150.0\n");  // invalide : trop haut
            writer.write("-60.0\n");  // nvalide : trop bas
            writer.write("20.0\n"); // valide
            writer.write("abc\n");    // invalide : non numérique
        }

        // initialiser SimulationDataParser avec une cellule RegularCell
        simulationDataParser = new SimulationDataParser(new RegularCell(0, 0, 0));
        Queue<Double> temperatureData = simulationDataParser.getTemperatureData();

        assertEquals(2, temperatureData.size());
        assertEquals(7.32, temperatureData.poll(), 0.01);
        assertEquals(20.0, temperatureData.poll(), 0.01);
    }

    @Test
    public void testFileWithOnlyInvalidTemperaturesThrowsException() {
        // ecrire que des températures invalides
        Exception exception = assertThrows(IOException.class, () -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) { // on utilise un try-with-resources pour s'assurer que le fichier est fermé après l'écriture
                writer.write("150.0\n"); // invalide : trop haut
                writer.write("-60.0\n"); // invalide : trop bas
                writer.write("abc\n");   // invalide : non numérique
            }
            // essayer d'initialiser SimulationDataParser ce qui devrait lever une IOException
            new SimulationDataParser(new RegularCell(0, 0, 0));
        });

        // vérifier que l'exception a le bon message
        assertTrue(exception.getMessage().contains("No valid temperatures found in the file."));
    }

    @Test
    public void testInvalidLineLengthThrowsException() {
        // ecrire une ligne trop longue
        Exception exception = assertThrows(IOException.class, () -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write("123456\n"); // Ligne trop longue
            }
            // essayer d'initialiser SimulationDataParser, ce qui devrait lever une IOException
            new SimulationDataParser(new RegularCell(0, 0, 0));
        });

        // vérifier que l'exception a le bon message
        assertTrue(exception.getMessage().contains("Invalid line length"));
    }
}
