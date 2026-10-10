package com.edt.controller;

import com.edt.model.Enseignant;
import com.edt.model.Matiere;
import com.edt.service.EnseignantService;
import com.edt.service.MatiereService;
import com.edt.util.AlertUtil;
import com.edt.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.sql.SQLException;

public class EnseignantController {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Enseignant> table;
    @FXML private TableColumn<Enseignant, String> colNom;
    @FXML private TableColumn<Enseignant, String> colPrenom;
    @FXML private TableColumn<Enseignant, String> colEmail;
    @FXML private TableColumn<Enseignant, String> colTelephone;
    @FXML private TableColumn<Enseignant, String> colSpecialite;
    @FXML private TableColumn<Enseignant, String> colMatiere;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private Label lblTelephoneInfo;
    @FXML private TextField txtSpecialite;
    @FXML private ComboBox<Matiere> cbMatiere;

    private final EnseignantService service = new EnseignantService();
    private final MatiereService matiereService = new MatiereService();
    private final ObservableList<Enseignant> donnees = FXCollections.observableArrayList();
    private Enseignant selection;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(d -> d.getValue().nomProperty());
        colPrenom.setCellValueFactory(d -> d.getValue().prenomProperty());
        colEmail.setCellValueFactory(d -> d.getValue().emailProperty());
        colTelephone.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTelephone() == null ? "" : d.getValue().getTelephone()));
        colSpecialite.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSpecialite() == null ? "" : d.getValue().getSpecialite()));
        colMatiere.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMatiereNom()));

        configurerChampTelephone();

        table.setItems(donnees);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, nouveau) -> {
            selection = nouveau;
            remplirFormulaire(nouveau);
        });

        txtRecherche.textProperty().addListener((obs, old, val) -> rechercher());

        chargerMatieres();
        charger();
    }

    /**
     * Le champ téléphone n'accepte que des chiffres et s'arrête automatiquement
     * à 10 caractères : impossible d'en saisir 11. Un indicateur "n/10" prévient
     * l'utilisateur tant que le compte n'y est pas.
     */
    private void configurerChampTelephone() {
        txtTelephone.setTextFormatter(new TextFormatter<>(change -> {
            String nouveauTexte = change.getControlNewText();
            if (nouveauTexte.matches("[0-9]{0," + ValidationUtil.LONGUEUR_TELEPHONE + "}")) {
                return change;
            }
            return null; // saisie refusée (lettre, symbole ou 11e chiffre)
        }));

        txtTelephone.textProperty().addListener((obs, old, val) -> majIndicateurTelephone(val));
        majIndicateurTelephone(txtTelephone.getText());
    }

    private void majIndicateurTelephone(String valeur) {
        if (lblTelephoneInfo == null) {
            return;
        }
        int saisis = valeur == null ? 0 : valeur.length();
        lblTelephoneInfo.setText(saisis + "/" + ValidationUtil.LONGUEUR_TELEPHONE + " chiffres");
        boolean complet = saisis == ValidationUtil.LONGUEUR_TELEPHONE;
        lblTelephoneInfo.setStyle(complet
                ? "-fx-text-fill: #2e7d32; -fx-font-size: 11px;"
                : "-fx-text-fill: #c62828; -fx-font-size: 11px;");
    }

    private void chargerMatieres() {
        try {
            cbMatiere.setItems(FXCollections.observableArrayList(matiereService.listerTout()));
        } catch (SQLException e) {
            AlertUtil.showError("Impossible de charger les matières : " + e.getMessage());
        }
    }

    private void charger() {
        try {
            donnees.setAll(service.listerTout());
        } catch (SQLException e) {
            AlertUtil.showError("Impossible de charger les enseignants : " + e.getMessage());
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
            Enseignant e = new Enseignant();
            renseigner(e);
            service.ajouter(e);
            AlertUtil.showInfo("Enseignant ajouté avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner un enseignant dans le tableau.");
            return;
        }
        try {
            renseigner(selection);
            service.modifier(selection);
            AlertUtil.showInfo("Enseignant modifié avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner un enseignant dans le tableau.");
            return;
        }
        if (!AlertUtil.showConfirmation("Confirmez-vous la suppression de cet enseignant ?")) {
            return;
        }
        try {
            service.supprimer(selection.getId());
            AlertUtil.showInfo("Enseignant supprimé.");
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
        txtPrenom.clear();
        txtEmail.clear();
        txtTelephone.clear();
        txtSpecialite.clear();
        cbMatiere.getSelectionModel().clearSelection();
        cbMatiere.setValue(null);
    }

    private void renseigner(Enseignant e) {
        e.setNom(txtNom.getText());
        e.setPrenom(txtPrenom.getText());
        e.setEmail(txtEmail.getText());
        e.setTelephone(txtTelephone.getText() == null ? "" : txtTelephone.getText().trim());
        e.setSpecialite(txtSpecialite.getText());
        e.setMatiere(cbMatiere.getValue());
    }

    private void remplirFormulaire(Enseignant e) {
        if (e == null) {
            return;
        }
        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        txtEmail.setText(e.getEmail());
        // Les anciennes fiches peuvent contenir des espaces ou un indicatif :
        // on ne réinjecte que les chiffres pour rester compatible avec le filtre.
        String chiffres = ValidationUtil.nettoyerChiffres(e.getTelephone());
        if (chiffres.length() > ValidationUtil.LONGUEUR_TELEPHONE) {
            chiffres = chiffres.substring(chiffres.length() - ValidationUtil.LONGUEUR_TELEPHONE);
        }
        txtTelephone.setText(chiffres);
        txtSpecialite.setText(e.getSpecialite());
        cbMatiere.setValue(e.getMatiere());
    }
}
