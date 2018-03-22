package seng302.Controllers;

import com.sun.javafx.scene.control.Logging;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import seng302.Core.Clinician;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    @FXML
    private Button accountSettingsButton;

    private int resultsPerPage;
    private int page = 1;
    private ArrayList<Donor> donorsFound;

    private ArrayList<UserWindowController> userWindows = new ArrayList<>();


    private ObservableList<Donor> currentPage = FXCollections.observableArrayList();

    ObservableList<Object> donors;

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
     * Function which is called when the user wants to update their account settings in the user Window,
     * and creates a new account settings window to do so. Then does a prompt for the password as well.
     */
    public void accountSettings() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");

        Optional<String> password = dialog.showAndWait();
        if(password.isPresent()){ //Ok was pressed, Else cancel
            if(password.get().equals(clinician.getPassword())){
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/clinicianAccountSettings.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 290, 350));
                    stage.initModality(Modality.APPLICATION_MODAL);

                    Main.setCurrentClinicianForAccountSettings(clinician);

                    stage.showAndWait();
                } catch (Exception e) {
                    System.out.println("here");
                    e.printStackTrace();
                }
            }else{ // Password incorrect
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Incorrect");
                alert.setHeaderText("Incorrect password. ");
                alert.setContentText("Please enter the correct password to view account settings");
                alert.show();
            }
        }
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


    /**
     * Updates theO ObservableList for the profile table
     */
    public void displayCurrentPage() {
        currentPage.clear();
        currentPage.addAll(getCurrentPage());
    }

    /**
     * Updates the list of donors found from the search
     * @param searchTerm the search term
     */
    public void updateFoundDonors(String searchTerm){
        donorsFound = Main.getDonorsByNameAlternative(searchTerm);
        donors = FXCollections.observableArrayList(donorsFound);

    }


    /**
     * Splits the sorted list of found donors and returns a page worth
     */
    public ObservableList<Donor> getCurrentPage(){
        int firstIndex = Math.max((page-1),0)*resultsPerPage;
        int lastIndex = Math.min(donors.size(), page*resultsPerPage);
        if(lastIndex<firstIndex){
            System.out.println(firstIndex+" to "+lastIndex+ " is an illegal page");
            return FXCollections.observableArrayList(new ArrayList<Donor>());
        }
        return FXCollections.observableArrayList(new ArrayList(donors.subList(firstIndex, lastIndex)));
    }

    /**
     * Displays the next page of results.
     */
    public void nextPage(){
        page++;
        updatePageButtons();
        updateResultsSummary();
        displayCurrentPage();
    }

    /**
     * Updates the resultsDisplayLabel to show how many results were found,
     * and how many are displayed.
     */
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

    /**
     * Displays the next page of donor search results
     */
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

        profileTable.setItems(currentPage);

        Main.setClinicianController(this);

        updateFoundDonors("");
        updatePageButtons();
        displayCurrentPage();
        updateResultsSummary();

        profileTable.setItems(currentPage);

        /**
         * RowFactory for the profileTable.
         * Displays a tooltip when the mouse is over a table entry.
         * Adds a mouse click listener to each row in the table so that a donor window
         * is opened when the event is triggered
         */
        profileTable.setRowFactory(new Callback<TableView<Donor>, TableRow<Donor>>() {
            @Override
            public TableRow<Donor> call(TableView<Donor> tableView) {
                final TableRow<Donor> row = new TableRow<Donor>() {
                    private Tooltip tooltip = new Tooltip();
                    @Override
                    public void updateItem(Donor donor, boolean empty) {
                        super.updateItem(donor, empty);
                        if (donor == null || empty) {
                            setTooltip(null);
                        } else {
                            if (donor.getOrgans().isEmpty()) {
                                tooltip.setText(donor.getName() + ".");
                            } else {
                                String organs = donor.getOrgans().toString();
                                tooltip.setText(donor.getName() + ". Donor: " + organs.substring(1, organs.length() - 1));
                            }
                            setTooltip(tooltip);
                        }
                    }
                };
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount()==2) {
                        System.out.println(row.getItem());
                        Stage stage = new Stage();

                        Main.addCliniciansDonorWindow(stage);
                        stage.initModality(Modality.NONE);

                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userWindow.fxml"));
                            Parent root = (Parent) loader.load();
                            UserWindowController userWindowController = loader.getController();
                            userWindowController.setCurrentDonor(row.getItem());
                            userWindowController.populateDonorFields();
                            Scene newScene = new Scene(root, 900, 575);
                            stage.setScene(newScene);
                            stage.show();
                        } catch (IOException | NullPointerException e) {
                            System.err.println("Unable to load fxml or save file.");
                            e.printStackTrace();
                            Platform.exit();
                        }
                    }
                });
                return row;
            }
        });

        /**
         * Sorts of the profileTable across all pages.
         * As items are removed and re-added, multiple sort calls can trigger an
         * IndexOutOfBoundsException exception.
         */
        profileTable.setSortPolicy(new Callback<TableView, Boolean>() {
            @Override public Boolean call(TableView table) {
                try{
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
