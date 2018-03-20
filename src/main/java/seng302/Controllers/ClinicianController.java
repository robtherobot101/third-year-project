package seng302.Controllers;

import com.sun.javafx.scene.control.Logging;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Duration;
import seng302.Core.Clinician;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ClinicianController implements Initializable {


    private Clinician clinician;

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(1000)
    );
    @FXML
    private TableColumn profileName;

    @FXML
    private TableColumn profileAge;

    @FXML
    private TableColumn profileGender;

    @FXML
    private TableColumn profileRegion;

    @FXML
    private TableView profileTable;
    @FXML
    private TextField profileSearchTextField;
    @FXML
    private Pane background;
    @FXML
    private TextField nameInput;
    @FXML
    private Label staffIDLabel;
    @FXML
    private TextField addressInput;
    @FXML
    private TextField regionInput;
    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label updatedSuccessfully;

    @FXML
    private Button nextPageButton;

    @FXML
    private Button previousPageButton;

    @FXML
    private Label resultsDisplayLabel;

    private int resultsPerPage;
    private int page = 1;
    private ArrayList<Donor> donorsFound;

    ObservableList<Object> donors;
    ObservableList currentPage = FXCollections.observableArrayList();

    /**
     * Sets the current clinician
     * @param clinician The clinician to se as the current
     */
    public void setClinician(Clinician clinician) {
        this.clinician = clinician;
        updateDisplay();
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current clinician
     */
    public void updateDisplay() {
        nameInput.setText(clinician.getName());
        staffIDLabel.setText(Long.toString(clinician.getStaffID()));
        addressInput.setText(clinician.getWorkAddress());
        regionInput.setText(clinician.getRegion());
    }

    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void updateClinician() {
        clinician.setName(nameInput.getText());
        clinician.setWorkAddress(addressInput.getText());
        clinician.setRegion(regionInput.getText());
        updatedSuccessfully.setOpacity(1.0);
        fadeIn.playFromStart();

        System.out.println("Updated to: " + clinician);
    }

    /**
     * Saves the clinician ArrayList to a JSON file
     */
    public void save(){
        Main.saveUsers(Main.getClinicianPath(), false);
    }

    /**
     * Closes the application
     */
    public void close(){
        Platform.exit();
    }

    /**
     * Changes the focus to the pane when pressed
     */
    public void requestFocus() { background.requestFocus(); }


    public void displayCurrentPage() {
        currentPage.clear();
        currentPage.addAll(getCurrentPage());
    }

    public void updateFoundDonors(String searchTerm){
        donorsFound = Main.getDonorsByNameAlternative(searchTerm);
        donors = FXCollections.observableArrayList(donorsFound);

    }

    public ObservableList<Donor> getCurrentPage(){
        int firstIndex = Math.max((page-1),0)*resultsPerPage;
        int lastIndex = Math.min(donors.size(), page*resultsPerPage);
        if(lastIndex<firstIndex){
            System.out.println(firstIndex+" to "+lastIndex+ " is an illegal page");
            return FXCollections.observableArrayList(new ArrayList<Donor>());
        }
        return FXCollections.observableArrayList(new ArrayList(donors.subList(firstIndex, lastIndex)));
    }

    public void nextPage(){
        page++;
        updatePageButtons();
        updateResultsSummary();
        displayCurrentPage();
    }

    public void updateResultsSummary(){
        String text;
        if(donorsFound.size()==0){
            text = "No results found";
        }else{
            int from = ((page-1)*resultsPerPage)+1;
            int to = Math.min((page*resultsPerPage), donorsFound.size());
            int of = donorsFound.size();
            text = "Displaying " + from + "-" + to + " of " + of;
        }
        resultsDisplayLabel.setText(text);
    }

    public void previousPage(){
        page--;
        updatePageButtons();
        updateResultsSummary();
        displayCurrentPage();
    }

    /**
     * Enables and disables the next and previous page buttons as necessary.
     */
    public void updatePageButtons(){
        if((page)*resultsPerPage>=donorsFound.size()){
            nextPageButton.setDisable(true);
        }else{
            nextPageButton.setDisable(false);
        }
        if(page==1){
            previousPageButton.setDisable(true);
        }else{
            previousPageButton.setDisable(false);
        }
    }

    public void sort(){
        for(Object t: profileTable.getSortOrder()){
            System.out.println(t);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultsPerPage = 15;
        profileSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            updateFoundDonors(newValue);
            updatePageButtons();
            displayCurrentPage();
            updateResultsSummary();
        });
        profileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        profileAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        profileGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        profileRegion.setCellValueFactory(new PropertyValueFactory<>("region"));

        fadeIn.setNode(updatedSuccessfully);
        fadeIn.setDelay(Duration.millis(1000));
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.setCycleCount(0);
        fadeIn.setAutoReverse(false);
        Main.setClinicianController(this);

        updateFoundDonors("");
        updatePageButtons();
        displayCurrentPage();
        updateResultsSummary();

        profileTable.setItems(currentPage);
        /**
         * Sorts of the profileTable across all pages.
         * As items are removed and re-added, multiple sort calls can trigger an
         * IndexOutOfBoundsException exception.
         */
        profileTable.setSortPolicy(new Callback<TableView, Boolean>() {
            @Override public Boolean call(TableView table) {
                try{
                    ObservableList<?> itemsList = donors;

                    if (itemsList == null || itemsList.isEmpty()) {
                        return true;
                    }

                    Comparator comparator = table.getComparator();
                    if (comparator == null) {
                        return true;
                    }
                    FXCollections.sort(donors, comparator);
                    displayCurrentPage();
                    profileTable.getSelectionModel().select(0);
                    return true;
                }catch(IndexOutOfBoundsException e){
                    System.out.println("Error");
                    return false;
                }catch(UnsupportedOperationException e){
                    return false;
                }catch(NullPointerException e){
                    return false;
                }
            }
        });
    }
}
