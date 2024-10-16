package com.example.view;

import com.example.model.Cell;
import com.example.model.DeadCell;
import com.example.model.HeatSourceCell;
import com.example.model.RegularCell;
import com.example.model.Grid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class CellMenuView {
    private Stage stage;
    private Cell cell;
    private GridPane gridPane;
    private Label positionLabel;
    private Label tempLabel;
    private CheckBox deadCellCheckBox;
    private CheckBox heatSourceCheckBox;
    private TextField heatSourceTempField;
    private Button validateButton;
    private Grid grid;

    public CellMenuView(Cell cell, Grid grid) {
        this.cell = cell;
        this.grid = grid; // reference à la grid pour la mettre à jour en fonction de ce que l'user met ici
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // pour bloquer la fenêtre principale!

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // affiche la position de la cellule dans la grid
        positionLabel = new Label("Position de la cellule : (" + cell.getRow() + ", " + cell.getCol() + ")");
        gridPane.add(positionLabel, 0, 0, 2, 1);

       if (!(cell instanceof DeadCell)) { // afficher la température seulement si la cellule n'est pas morte
    tempLabel = new Label(String.format("Température de la cellule : %.2f°C", cell.getTemp()));
    gridPane.add(tempLabel, 0, 1, 2, 1);
}

        deadCellCheckBox = new CheckBox("Définir comme cellule morte");
        deadCellCheckBox.setSelected(cell instanceof DeadCell);
        gridPane.add(deadCellCheckBox, 0, 2);

        heatSourceCheckBox = new CheckBox("Définir comme source de chaleur");
        heatSourceCheckBox.setSelected(cell instanceof HeatSourceCell);
        gridPane.add(heatSourceCheckBox, 0, 3);

        heatSourceTempField = new TextField();
        heatSourceTempField.setPromptText("T° de la source");
        if (cell instanceof HeatSourceCell) {
            heatSourceTempField.setText(String.valueOf(cell.getTemp()));
        } else {
            heatSourceTempField.setDisable(true); // desac le champ au depart si ce n'est pas une source de chaleur
        }
        gridPane.add(heatSourceTempField, 1, 3);

        validateButton = new Button("Valider");
        gridPane.add(validateButton, 0, 4, 2, 1);

        stage.setScene(new Scene(gridPane, 400, 300));  // taille ajustée de la fenêtre
        stage.setTitle("Cellule (" + cell.getRow() + ", " + cell.getCol() + ")");
        stage.show();

        addEventHandlers();
    }

    private void addEventHandlers() {
        // empecher l'user de cocher les 2 cases en meme temps
        deadCellCheckBox.setOnAction(event -> {
            if (deadCellCheckBox.isSelected()) {
                heatSourceCheckBox.setSelected(false);
                heatSourceTempField.setDisable(true);
            } else {
                heatSourceTempField.setDisable(false);
            }
        });

        heatSourceCheckBox.setOnAction(event -> {
            if (heatSourceCheckBox.isSelected()) {
                deadCellCheckBox.setSelected(false);
                heatSourceTempField.setDisable(false);
            } else {
                heatSourceTempField.setDisable(true);
            }
        });

        // gerer la validation et la mise à jour de la cellule
        validateButton.setOnAction(event -> {
            if (deadCellCheckBox.isSelected()) {
                cell = new DeadCell(cell.getRow(), cell.getCol(), 0);
            } else if (heatSourceCheckBox.isSelected()) {
                String tempText = heatSourceTempField.getText().trim();
                if (tempText.isEmpty()) {
                    showAlert("Erreur", "Veuillez entrer une température valide pour la source de chaleur.");
                    return;
                }
                try {
                    double temp = Double.parseDouble(tempText);
                    if (temp < cell.getMinTemp() || temp > cell.getMaxTemp()) {
                        showAlert("Erreur", "La température doit être comprise entre " + cell.getMinTemp() + " et " + cell.getMaxTemp() + "°C.");
                        return;
                    }
                    cell = new HeatSourceCell(cell.getRow(), cell.getCol(), temp);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le format de la température est incorrect.");
                    return;
                }
            } else {
                cell = new RegularCell(cell.getRow(), cell.getCol(), cell.getTemp());
            }

            grid.setCell(cell); // mettre a jour la grid avec la nouvelle cellule
            grid.notifyObservers(); // notifier la grid pour qu'elle se rafraîchisse
            stage.close();
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        stage.show();
    }

    // methode pour afficher une alerte si l'user fait une erreur
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
