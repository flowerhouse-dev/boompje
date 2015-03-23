/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import domain.Administratie;
import domain.Geslacht;
import domain.Gezin;
import domain.Persoon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import controller.StamboomController;
import domain.*;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //GEZIN
    @FXML ComboBox  cbGezinnen;
    @FXML TextField tfGezinDatum;
    @FXML Button btnVoegHuwelijksDatumToe;
    @FXML Button btwVoegScheidingsDatumToe;
    @FXML TextField tfGezinNr;
    @FXML TextField tfGezinOuder1;
    @FXML TextField tfGezinOuder2;
    @FXML TextField tfGezinHuwelijksDatum;
    @FXML TextField tfGezinScheidingsDatum;
    @FXML ListView lvGezinKinderen;
    
    //INVOER PERSOON
    @FXML ComboBox cbNieuwPersoonGeslacht;
    @FXML TextField tfNieuwPersoonVoornamen;
    @FXML TextField tfNieuwPersoonTussenvoegsels;
    @FXML TextField tfNieuwPersoonAchternaam;
    @FXML TextField tfNieuwPersoonGeboorteDatum;
    @FXML TextField tfNieuwPersoonGeboortePlaats;
    @FXML ComboBox  cbNieuwPersoonOuderlijkGezin;
    @FXML Button btnNieuwGezinCancel;
    @FXML Button btnNieuwGezinOk;
    
    //STAMBOOM
    @FXML Button btnStamboomOpslaan;
    @FXML Button btnStamboomLaden;
    @FXML TextArea taStamboom;

    //opgave 4
    private boolean withDatabase;
    private Administratie administratie;
    private StamboomController controller;
    
    private Gezin huidigGezin;
    private Persoon huidigPersoon;
    
    private Persoon piet, teuntje;
    private Gezin pietEnTeuntje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        controller = new StamboomController();
        administratie = new Administratie();
        piet = administratie.addPersoon(Geslacht.MAN, new String[]{"Piet", "Franciscus"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede", null);
        teuntje = administratie.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1949, Calendar.MAY, 5), "Amersfoort", null);
        pietEnTeuntje = administratie.addOngehuwdGezin(piet, teuntje);
        
        initComboboxes();
        withDatabase = false;
        
        
        maakReadOnly(tfPersoonNr);
        maakReadOnly(tfVoornamen);
        maakReadOnly(tfTussenvoegsel);
        maakReadOnly(tfAchternaam);
        maakReadOnly(tfGeslacht);
        maakReadOnly(tfGebDatum);
        maakReadOnly(tfGebPlaats);
        maakReadOnly(tfGezinNr);
        maakReadOnly(tfGezinOuder1);
        maakReadOnly(tfGezinOuder2);
        maakReadOnly(tfGezinHuwelijksDatum);
        maakReadOnly(tfGezinScheidingsDatum);
    }

    private void initComboboxes() {
        //todo opgave 3 
        
        if (!this.withDatabase) {
            try {
                this.cbPersonen.setItems(this.administratie.getPersonen());
                this.cbGezinnen.setItems(this.administratie.getGezinnen());
                this.cbOuder1Invoer.setItems(this.administratie.getPersonen());
                this.cbOuder2Invoer.setItems(this.administratie.getPersonen());
                this.cbNieuwPersoonOuderlijkGezin.setItems(this.administratie.getGezinnen());
                this.cbNieuwPersoonGeslacht.setItems(this.administratie.getGeslachten());
            } catch (NullPointerException e) {
                System.out.println(e);
            }
            
        }
    }

    public void selectPersoon(Event evt) {
        this.huidigPersoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(this.huidigPersoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
                cbOuderlijkGezin.getItems().clear();
            }

            //todo opgave 3
            if (!persoon.getAlsOuderBetrokkenIn().isEmpty()) {
                lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
            }
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = administratie.getPersoon(nr);
        administratie.setOuders(p, ouderlijkGezin);
    }

    public void selectGezin(Event evt) {
        // todo opgave 3
        this.huidigGezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        showGezin(this.huidigGezin);
    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3

        if (gezin == null) {
            clearTabGezin();
        } else {
            tfGezinNr.setText(gezin.getNr() + "");
            tfGezinOuder1.setText(gezin.getOuder1().toString());
            tfGezinOuder2.setText(gezin.getOuder2().toString());
            
            if (gezin.getHuwelijksdatum() != null) {
                tfGezinHuwelijksDatum.setText(StringUtilities.datumString(gezin.getHuwelijksdatum()));
            }
            
            if (gezin.getScheidingsdatum() != null) {
                tfGezinScheidingsDatum.setText(StringUtilities.datumString(gezin.getScheidingsdatum()));
            }
            
            if (!gezin.getKinderen().isEmpty()) {
                lvGezinKinderen.setItems(gezin.getKinderen());
            }
        }
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3

        if (this.huidigGezin != null && checkDatum(tfGezinDatum.getText())) {
            if (!this.administratie.setHuwelijk(huidigGezin, new GregorianCalendar(
                    Integer.parseInt(tfGezinDatum.getText().substring(6)), 
                    getMaand(tfGezinDatum.getText()), 
                    Integer.parseInt(tfGezinDatum.getText().substring(0, 2))))) {
                showDialog("Warning", "Huwelijksdatum toevoegen mislukt.");
            }
            showGezin(huidigGezin);
        } else {
            showDialog("Warning", "Datum onjuist.");
        }
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3

        if (this.huidigGezin != null && checkDatum(tfGezinDatum.getText())) {
            if (!this.administratie.setScheiding(huidigGezin, new GregorianCalendar(
                    Integer.parseInt(tfGezinDatum.getText().substring(6)), 
                    getMaand(tfGezinDatum.getText()), 
                    Integer.parseInt(tfGezinDatum.getText().substring(0, 2))))) {
                showDialog("Warning", "Scheidingsdatum toevoegen mislukt.");
            }
            showGezin(huidigGezin);
        } else {
            showDialog("Warning", "Datum onjuist.");
        }
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        Geslacht geslacht;
        if(cbNieuwPersoonGeslacht.getSelectionModel().getSelectedItem() == Geslacht.MAN)
        {
           geslacht = Geslacht.MAN;
        }
        else
        {
            geslacht = Geslacht.VROUW;
        }
        String[] vnamen;
        String vnaam = tfNieuwPersoonVoornamen.getText();
        vnamen = vnaam.split(" ");
        Calendar geboortedatum;
        try {
            geboortedatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "geboortedatum :" + exc.getMessage());
            return;
        }
        Gezin gezin =(Gezin) cbNieuwPersoonOuderlijkGezin.getSelectionModel().getSelectedItem();
       administratie.addPersoon(geslacht, vnamen,tfNieuwPersoonAchternaam.getText(), tfNieuwPersoonTussenvoegsels.getText(), geboortedatum, tfNieuwPersoonGeboortePlaats.getText(), gezin);

    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = administratie.addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    administratie.setScheiding(g, scheidingsdatum);
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = administratie.addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // todo opgave 3
        
        taStamboom.setText(this.huidigPersoon.stamboomAlsString());
    }

    public void createEmptyStamboom(Event evt) {
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        File opslag = new File("Stamboom");
       
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
        File opslag = new File("Stamboom");
        if (opslag.exists()) {
            opslag.delete();
        }
            administratie.aantalGeregistreerdePersonen();
        
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        //todo opgave 3
        
        cbNieuwPersoonGeslacht.getSelectionModel().clearSelection();
        tfNieuwPersoonVoornamen.clear();
        tfNieuwPersoonTussenvoegsels.clear();
        tfNieuwPersoonAchternaam.clear();
        tfNieuwPersoonGeboorteDatum.clear();
        tfNieuwPersoonGeboortePlaats.clear();
        cbNieuwPersoonOuderlijkGezin.getSelectionModel().clearSelection();
    }

    
    private void clearTabGezinInvoer() {
        //todo opgave 3
        
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        // todo opgave 3
       
        cbGezinnen.getSelectionModel().clearSelection();
       // cbGezinnen.setItems(administratie.getGezinnen());
        tfGezinNr.clear();
        tfGezinOuder1.clear();
        tfGezinOuder2.clear();
        tfGezinHuwelijksDatum.clear();
        tfGezinScheidingsDatum.clear();
        lvGezinKinderen.setItems(FXCollections.emptyObservableList());
    }

    private void showDialog(String type, String message) {
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }
    
    private void maakReadOnly(TextField tf) {
        tf.setDisable(true);
        tf.setOpacity(1.0);
    }

    private boolean checkDatum(String datum) {
        if (!datum.substring(0, 2).matches("\\d+") || 
                !datum.substring(3, 5).matches("\\d+") || 
                !datum.substring(6).matches("\\d+") ||
                !datum.substring(2, 3).matches("-") ||
                !datum.substring(5, 6).matches("-")) {
            return false;
        } else {
            return true;
        }
    }
    
    private int getMaand(String maand) {
        String mm = maand.substring(3, 5);
        
        switch (mm) {
            case "01":
                return Calendar.JANUARY;
            case "02":
                return Calendar.FEBRUARY;
            case "03":
                return Calendar.MARCH;
            case "04":
                return Calendar.APRIL;
            case "05":
                return Calendar.MAY;
            case "06":
                return Calendar.JUNE;
            case "07":
                return Calendar.JULY;
            case "08":
                return Calendar.AUGUST;
            case "09":
                return Calendar.SEPTEMBER;
            case "10":
                return Calendar.OCTOBER;
            case "11":
                return Calendar.NOVEMBER;
            case "12":
                return Calendar.DECEMBER;
        }  
        
        return 0;
    }

    private Object getAdministratie() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
