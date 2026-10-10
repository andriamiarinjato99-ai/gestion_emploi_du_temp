package com.edt.controller;

import com.edt.model.Matiere;
import com.edt.service.MatiereService;
import com.edt.util.AlertUtil;
import com.edt.util.ValidationUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class MatiereController {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Matiere> table;
    @FXML private TableColumn<Matiere, String> colCode;
    @FXML private TableColumn<Matiere, String> colNom;
    @FXML private TableColumn<Matiere, Number> colVolume;

    @FXML private TextField txtCode;
    @FXML private TextField txtNom;
    @FXML private TextField txtVolume;

    private final MatiereService service = new MatiereService();
    private final ObservableList<Matiere> donnees = FXCollections.observableArrayList();
    private Matiere selection;

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(d -> d.getValue().codeProperty());
        colNom.setCellValueFactory(d -> d.getValue().nomProperty());
        colVolume.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getVolumeHoraire()));

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
            AlertUtil.showError("Impossible de charger les matières : " + e.getMessage());
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
            Matiere m = new Matiere();
            renseigner(m);
            service.ajouter(m);
            AlertUtil.showInfo("Matière ajoutée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une matière dans le tableau.");
            return;
        }
        try {
            renseigner(selection);
            service.modifier(selection);
            AlertUtil.showInfo("Matière modifiée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une matière dans le tableau.");
            return;
        }
        if (!AlertUtil.showConfirmation("Confirmez-vous la suppression de cette matière ?")) {
            return;
        }
        try {
            service.supprimer(selection.getId());
            AlertUtil.showInfo("Matière supprimée.");
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
        txtCode.clear();
        txtNom.clear();
        txtVolume.clear();
    }

    private void renseigner(Matiere m) {
        m.setCode(txtCode.getText());
        m.setNom(txtNom.getText());
        m.setVolumeHoraire(ValidationUtil.requirePositiveInt(txtVolume.getText(), "Volume horaire"));
    }

    private void remplirFormulaire(Matiere m) {
        if (m == null) {
            return;
        }
        txtCode.setText(m.getCode());
        txtNom.setText(m.getNom());
        txtVolume.setText(String.valueOf(m.getVolumeHoraire()));
    }
}
