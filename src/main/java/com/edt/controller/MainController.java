package com.edt.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnEnseignants;
    @FXML private Button btnMatieres;
    @FXML private Button btnClasses;
    @FXML private Button btnSalles;
    @FXML private Button btnSeances;

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        load("/com/edt/view/dashboard.fxml", btnDashboard);
    }

    @FXML
    public void showEnseignants() {
        load("/com/edt/view/enseignant.fxml", btnEnseignants);
    }

    @FXML
    public void showMatieres() {
        load("/com/edt/view/matiere.fxml", btnMatieres);
    }

    @FXML
    public void showClasses() {
        load("/com/edt/view/classe.fxml", btnClasses);
    }

    @FXML
    public void showSalles() {
        load("/com/edt/view/salle.fxml", btnSalles);
    }

    @FXML
    public void showSeances() {
        load("/com/edt/view/seance.fxml", btnSeances);
    }

    private void load(String fxmlPath, Button activeButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
            highlight(activeButton);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger la vue : " + fxmlPath, e);
        }
    }

    private void highlight(Button active) {
        for (Button b : new Button[]{btnDashboard, btnEnseignants, btnMatieres, btnClasses, btnSalles, btnSeances}) {
            b.getStyleClass().remove("nav-button-active");
        }
        active.getStyleClass().add("nav-button-active");
    }
}
