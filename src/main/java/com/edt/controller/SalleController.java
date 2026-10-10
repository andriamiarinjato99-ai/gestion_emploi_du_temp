package com.edt.controller;

import com.edt.model.Salle;
import com.edt.service.SalleService;
import com.edt.util.AlertUtil;
import com.edt.util.ValidationUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class SalleController {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Salle> table;
    @FXML private TableColumn<Salle, String> colNom;
    @FXML private TableColumn<Salle, Number> colCapacite;
    @FXML private TableColumn<Salle, String> colType;

    @FXML private TextField txtNom;
    @FXML private TextField txtCapacite;
    @FXML private TextField txtType;

    private final SalleService service = new SalleService();
    private final ObservableList<Salle> donnees = FXCollections.observableArrayList();
    private Salle selection;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(d -> d.getValue().nomProperty());
        colCapacite.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCapacite()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getType() == null ? "" : d.getValue().getType()));

        table.setItems(donnees);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, nouveau) -> {
            selection = nouveau;
            remplirFormulaire(nouveau);
        });

        txtRecherche.textProperty().addListener((obs, old, val) -> rechercher());

        charger();
    }

    private void charger() {
        try {
            donnees.setAll(service.listerTout());
        } catch (SQLException e) {
            AlertUtil.showError("Impossible de charger les salles : " + e.getMessage());
        }
    }

    private void rechercher() {
        try {
            donnees.setAll(service.rechercher(txtRecherche.getText()));
        } catch (SQLException e) {
            AlertUtil.showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    @FXML
    public void ajouter() {
        try {
            Salle s = new Salle();
            renseigner(s);
            service.ajouter(s);
            AlertUtil.showInfo("Salle ajoutée avec succès.");
            charger();
            effacer();
        } catch (IllegalArgumentException ex) {
            AlertUtil.showError(ex.getMessage());
        } catch (SQLException ex) {
            AlertUtil.showError("Erreur base de données : " + ex.getMessage());
        }
    }

    @FXML
    public void modifier() {
        if (selection == null) {
            AlertUtil.showError("Veuillez sélectionner une salle dans le tableau.");
            return;
        }
        try {
            renseigner(selection);
            service.modifier(selection);
            AlertUtil.showInfo("Salle modifiée avec succès.");
            charger();
            effacer();
        } catch (IllegalArgumentException ex) {
            AlertUtil.showError(ex.getMessage());
        } catch (SQLException ex) {
            AlertUtil.showError("Erreur base de données : " + ex.getMessage());
        }
    }

    @FXML
    public void supprimer() {
        if (selection == null) {
            AlertUtil.showError("Veuillez sélectionner une salle dans le tableau.");
            return;
        }
        if (!AlertUtil.showConfirmation("Confirmez-vous la suppression de cette salle ?")) {
            return;
        }
        try {
            service.supprimer(selection.getId());
            AlertUtil.showInfo("Salle supprimée.");
            charger();
            effacer();
        } catch (SQLException ex) {
            AlertUtil.showError("Erreur base de données : " + ex.getMessage());
        }
    }

    @FXML
    public void effacer() {
        selection = null;
        table.getSelectionModel().clearSelection();
        txtNom.clear();
        txtCapacite.clear();
        txtType.clear();
    }

    private void renseigner(Salle s) {
        s.setNom(txtNom.getText());
        s.setCapacite(ValidationUtil.requirePositiveInt(txtCapacite.getText(), "Capacité"));
        s.setType(txtType.getText());
    }

    private void remplirFormulaire(Salle s) {
        if (s == null) {
            return;
        }
        txtNom.setText(s.getNom());
        txtCapacite.setText(String.valueOf(s.getCapacite()));
        txtType.setText(s.getType());
    }
}
