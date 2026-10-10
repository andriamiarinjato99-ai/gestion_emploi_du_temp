package com.edt.controller;

import com.edt.model.*;
import com.edt.service.*;
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

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SeanceController {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Seance> table;
    @FXML private TableColumn<Seance, String> colJour;
    @FXML private TableColumn<Seance, String> colHeureDebut;
    @FXML private TableColumn<Seance, String> colHeureFin;
    @FXML private TableColumn<Seance, String> colMatiere;
    @FXML private TableColumn<Seance, String> colEnseignant;
    @FXML private TableColumn<Seance, String> colClasse;
    @FXML private TableColumn<Seance, String> colSalle;

    // Ordre du formulaire : Enseignant d'abord, Matière juste en dessous.
    @FXML private ComboBox<Enseignant> cbEnseignant;
    @FXML private ComboBox<Matiere> cbMatiere;
    @FXML private Label lblMatiereInfo;
    @FXML private ComboBox<Classe> cbClasse;
    @FXML private ComboBox<Salle> cbSalle;
    @FXML private ComboBox<Jour> cbJour;
    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;

    private final SeanceService seanceService = new SeanceService();
    private final MatiereService matiereService = new MatiereService();
    private final EnseignantService enseignantService = new EnseignantService();
    private final ClasseService classeService = new ClasseService();
    private final SalleService salleService = new SalleService();

    private static final DateTimeFormatter HEURE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final ObservableList<Seance> donnees = FXCollections.observableArrayList();
    private Seance selection;

    /**
     * Vrai pendant que le formulaire est rempli par le programme (sélection dans
     * le tableau, effacement). Empêche le remplissage automatique de la matière
     * d'écraser la matière réelle de la séance sélectionnée.
     */
    private boolean chargementFormulaire = false;

    @FXML
    public void initialize() {
        colJour.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJour().toString()));
        colHeureDebut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHeureDebut().format(HEURE_FORMAT)));
        colHeureFin.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHeureFin().format(HEURE_FORMAT)));
        colMatiere.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMatiere().getNom()));
        colEnseignant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEnseignant().toString()));
        colClasse.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getClasse().getNom()));
        colSalle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSalle().getNom()));

        cbJour.setItems(FXCollections.observableArrayList(Jour.values()));

        table.setItems(donnees);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, nouveau) -> {
            selection = nouveau;
            remplirFormulaire(nouveau);
        });

        txtRecherche.textProperty().addListener((obs, old, val) -> rechercher());

        // Sélectionner un enseignant remplit automatiquement sa matière.
        cbEnseignant.valueProperty().addListener((obs, old, nouveau) -> {
            if (!chargementFormulaire) {
                remplirMatiereAutomatiquement(nouveau);
            }
        });

        // Correction automatique des heures : 8 -> 08:00, 8:30 -> 08:30
        installerCorrectionAutoHeure(txtHeureDebut);
        installerCorrectionAutoHeure(txtHeureFin);

        chargerListesDeroulantes();
        charger();
    }

    // =====================================================================
    //  Correction automatique des heures
    // =====================================================================

    /**
     * Applique la correction dès que le champ perd le focus ou que l'utilisateur
     * valide par Entrée : la saisie "8" devient "08:00", "8:30" devient "08:30".
     */
    private void installerCorrectionAutoHeure(TextField champ) {
        champ.focusedProperty().addListener((obs, avaitFocus, aLeFocus) -> {
            if (!aLeFocus) {
                corrigerHeure(champ);
            }
        });
        champ.setOnAction(event -> corrigerHeure(champ));
    }

    private void corrigerHeure(TextField champ) {
        String normalise = ValidationUtil.normaliserHeure(champ.getText());
        if (normalise != null && !normalise.equals(champ.getText())) {
            champ.setText(normalise);
        }
    }

    // =====================================================================
    //  Remplissage automatique de la matière
    // =====================================================================

    /**
     * Cherche la matière de l'enseignant : d'abord celle rattachée à sa fiche,
     * sinon celle qu'il assure le plus souvent dans les séances déjà saisies.
     * La matière reste modifiable manuellement.
     */
    private void remplirMatiereAutomatiquement(Enseignant enseignant) {
        if (enseignant == null) {
            afficherInfoMatiere("");
            return;
        }

        Matiere matiere = enseignant.getMatiere();
        if (matiere == null) {
            try {
                matiere = seanceService.matiereHabituelle(enseignant.getId());
            } catch (SQLException e) {
                matiere = null;
            }
        }

        if (matiere == null) {
            afficherInfoMatiere("Aucune matière enregistrée pour cet enseignant : à choisir manuellement.");
            return;
        }

        Matiere dansLaListe = trouverMatiereParId(matiere.getId());
        if (dansLaListe == null) {
            afficherInfoMatiere("Aucune matière enregistrée pour cet enseignant : à choisir manuellement.");
            return;
        }

        cbMatiere.setValue(dansLaListe);
        afficherInfoMatiere("Matière remplie automatiquement d'après l'enseignant (modifiable).");
    }

    private Matiere trouverMatiereParId(int id) {
        for (Matiere m : cbMatiere.getItems()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    private void afficherInfoMatiere(String message) {
        if (lblMatiereInfo != null) {
            lblMatiereInfo.setText(message);
        }
    }

    // =====================================================================
    //  Chargement des données
    // =====================================================================

    private void chargerListesDeroulantes() {
        try {
            cbMatiere.setItems(FXCollections.observableArrayList(matiereService.listerTout()));
            cbEnseignant.setItems(FXCollections.observableArrayList(enseignantService.listerTout()));
            cbClasse.setItems(FXCollections.observableArrayList(classeService.listerTout()));
            cbSalle.setItems(FXCollections.observableArrayList(salleService.listerTout()));
        } catch (SQLException e) {
            AlertUtil.showError("Impossible de charger les listes déroulantes : " + e.getMessage());
        }
    }

    private void charger() {
        try {
            donnees.setAll(seanceService.listerTout());
        } catch (SQLException e) {
            AlertUtil.showError("Impossible de charger les séances : " + e.getMessage());
        }
    }

    private void rechercher() {
        try {
            donnees.setAll(seanceService.rechercher(txtRecherche.getText()));
        } catch (SQLException e) {
            AlertUtil.showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    // =====================================================================
    //  Actions
    // =====================================================================

    @FXML
    public void ajouter() {
        try {
            Seance s = new Seance();
            s.setId(0);
            renseigner(s);
            seanceService.ajouter(s);
            AlertUtil.showInfo("Séance ajoutée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une séance dans le tableau.");
            return;
        }
        try {
            renseigner(selection);
            seanceService.modifier(selection);
            AlertUtil.showInfo("Séance modifiée avec succès.");
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
            AlertUtil.showError("Veuillez sélectionner une séance dans le tableau.");
            return;
        }
        if (!AlertUtil.showConfirmation("Confirmez-vous la suppression de cette séance ?")) {
            return;
        }
        try {
            seanceService.supprimer(selection.getId());
            AlertUtil.showInfo("Séance supprimée.");
            charger();
            effacer();
        } catch (SQLException ex) {
            AlertUtil.showError("Erreur base de données : " + ex.getMessage());
        }
    }

    @FXML
    public void effacer() {
        chargementFormulaire = true;
        try {
            selection = null;
            table.getSelectionModel().clearSelection();
            cbEnseignant.getSelectionModel().clearSelection();
            cbEnseignant.setValue(null);
            cbMatiere.getSelectionModel().clearSelection();
            cbMatiere.setValue(null);
            cbClasse.getSelectionModel().clearSelection();
            cbSalle.getSelectionModel().clearSelection();
            cbJour.getSelectionModel().clearSelection();
            txtHeureDebut.clear();
            txtHeureFin.clear();
            afficherInfoMatiere("");
        } finally {
            chargementFormulaire = false;
        }
    }

    private void renseigner(Seance s) {
        // On corrige les heures avant lecture : l'utilisateur peut cliquer
        // directement sur "Ajouter" sans quitter le champ.
        corrigerHeure(txtHeureDebut);
        corrigerHeure(txtHeureFin);

        s.setMatiere(cbMatiere.getValue());
        s.setEnseignant(cbEnseignant.getValue());
        s.setClasse(cbClasse.getValue());
        s.setSalle(cbSalle.getValue());
        s.setJour(cbJour.getValue());

        LocalTime debut = ValidationUtil.parseTime(txtHeureDebut.getText(), "Heure de début");
        LocalTime fin = ValidationUtil.parseTime(txtHeureFin.getText(), "Heure de fin");
        s.setHeureDebut(debut);
        s.setHeureFin(fin);
    }

    private void remplirFormulaire(Seance s) {
        if (s == null) {
            return;
        }
        chargementFormulaire = true;
        try {
            cbEnseignant.setValue(s.getEnseignant());
            cbMatiere.setValue(s.getMatiere());
            cbClasse.setValue(s.getClasse());
            cbSalle.setValue(s.getSalle());
            cbJour.setValue(s.getJour());
            txtHeureDebut.setText(s.getHeureDebut().format(HEURE_FORMAT));
            txtHeureFin.setText(s.getHeureFin().format(HEURE_FORMAT));
            afficherInfoMatiere("");
        } finally {
            chargementFormulaire = false;
        }
    }
}
