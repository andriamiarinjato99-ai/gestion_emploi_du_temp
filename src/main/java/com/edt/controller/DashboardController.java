package com.edt.controller;

import com.edt.model.Jour;
import com.edt.service.*;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class DashboardController {

    @FXML private Label lblNbEnseignants;
    @FXML private Label lblNbMatieres;
    @FXML private Label lblNbClasses;
    @FXML private Label lblNbSalles;
    @FXML private Label lblNbSeances;
    @FXML private BarChart<String, Number> chartSeances;

    private final EnseignantService enseignantService = new EnseignantService();
    private final MatiereService matiereService = new MatiereService();
    private final ClasseService classeService = new ClasseService();
    private final SalleService salleService = new SalleService();
    private final SeanceService seanceService = new SeanceService();

    @FXML
    public void initialize() {
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        try {
            lblNbEnseignants.setText(String.valueOf(enseignantService.compter()));
            lblNbMatieres.setText(String.valueOf(matiereService.compter()));
            lblNbClasses.setText(String.valueOf(classeService.compter()));
            lblNbSalles.setText(String.valueOf(salleService.compter()));
            lblNbSeances.setText(String.valueOf(seanceService.compter()));

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Séances");
            for (Jour jour : Jour.values()) {
                serie.getData().add(new XYChart.Data<>(jour.toString(), 0));
            }
            for (Object[] ligne : seanceService.statistiquesParJour()) {
                String jour = capitaliser((String) ligne[0]);
                int nb = (Integer) ligne[1];
                for (XYChart.Data<String, Number> point : serie.getData()) {
                    if (point.getXValue().equals(jour)) {
                        point.setYValue(nb);
                    }
                }
            }
            chartSeances.getData().clear();
            chartSeances.getData().add(serie);
        } catch (SQLException e) {
            lblNbEnseignants.setText("--");
            lblNbMatieres.setText("--");
            lblNbClasses.setText("--");
            lblNbSalles.setText("--");
            lblNbSeances.setText("--");
        }
    }

    private String capitaliser(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
