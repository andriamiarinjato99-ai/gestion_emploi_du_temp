package com.edt.controller;

import com.edt.model.Classe;
import com.edt.service.ClasseService;
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

public class ClasseController {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Classe> table;
    @FXML private TableColumn<Classe, String> colNom;
    @FXML private TableColumn<Classe, String> colNiveau;
    @FXML private TableColumn<Classe, Number> colEffectif;

    @FXML private TextField txtNom;
    @FXML private TextField txtNiveau;
    @FXML private TextField txtEffectif;

    private final ClasseService service = new ClasseService();
    private final ObservableList<Classe> donnees = FXCollections.observableArrayList();
    private Classe selection;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(d -> d.getValue().nomProperty());
        colNiveau.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNiveau() == null ? "" : d.getValue().getNiveau()));
        colEffectif.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getEffectif()));

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
            AlertUtil.showError("Impossible de charger les classes : " + e.getMessage());
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
            Classe c = new Classe();
            renseigner(c);
            service.ajouter(c);
            AlertUtil.showInfo("Classe ajoutée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une classe dans le tableau.");
            return;
        }
        try {
            renseigner(selection);
            service.modifier(selection);
            AlertUtil.showInfo("Classe modifiée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une classe dans le tableau.");
            return;
        }
        if (!AlertUtil.showConfirmation("Confirmez-vous la suppression de cette classe ?")) {
            return;
        }
        try {
            service.supprimer(selection.getId());
            AlertUtil.showInfo("Classe supprimée.");
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
        txtNiveau.clear();
        txtEffectif.clear();
    }

    private void renseigner(Classe c) {
        c.setNom(txtNom.getText());
        c.setNiveau(txtNiveau.getText());
        c.setEffectif(ValidationUtil.requirePositiveInt(txtEffectif.getText(), "Effectif"));
    }

    private void remplirFormulaire(Classe c) {
        if (c == null) {
            return;
        }
        txtNom.setText(c.getNom());
        txtNiveau.setText(c.getNiveau());
        txtEffectif.setText(String.valueOf(c.getEffectif()));
    }
}
