package seng302.gui.controllers.clinician;

import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import seng302.User.Attribute.NZRegion;
import seng302.User.Attribute.Organ;
import seng302.User.DonatableOrgan;
import seng302.User.User;
import seng302.generic.Country;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.gui.StatusIndicator;
import seng302.gui.TitleBar;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class ClinicianAvailableOrgansController implements Initializable{

    @FXML
    AnchorPane organsPane;
    @FXML
    TreeTableColumn<Object, String> organColumn;
    @FXML
    TreeTableColumn<Object, String> nameColumn;
    @FXML
    TreeTableColumn countdownColumn;
    @FXML
    TreeTableColumn<Object, String> dateOfDeathColumn;

    @FXML
    TreeTableColumn<Object, String> organRegionColumn;
    @FXML
    TreeTableColumn<Object, String> receiverRegionColumn;

    @FXML
    TreeTableView<Object> organsTreeTable;
    @FXML
    TextField receiverNameTextField;
    @FXML
    Label updateResultsLabel, refreshSuccessText;
    @FXML
    Button refreshOrganTable;
    @FXML
    Button organsFilterButton;
    @FXML
    ComboBox<String> organFilter;

    @FXML
    TextField regionTextField;
    @FXML
    ComboBox regionComboBox;
    @FXML
    ComboBox countryComboBox;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private Timer time = new Timer(true);
    private TimerTask tick;
    private String token;
    private boolean focused;
    private boolean updated;
    private FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(3500));
    private boolean autoRefresh;

    private String organApplied, regionApplied, nameApplied = "";


    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null;
    }

    List<DonatableOrgan> expiryList = new ArrayList<>();

    private String allRegions = "All Regions";
    private String allOrgans = "All Organs";

    /**
     * Sets the initial time left values for all list items.
     * @param expiryList A list of organs to be updated.
     */
    public void setInitTimeLeft(List<DonatableOrgan> expiryList){

        for (DonatableOrgan organ : expiryList){
            //for each item in the list
            LocalDateTime now = LocalDateTime.now();

            // Calculate the date the organ expires, based on the organ
            LocalDateTime deathDate = organ.getTimeOfDeath();
            Duration expiryDuration = organ.getExpiryDuration(organ.getOrganType());
            LocalDateTime expiryDate = deathDate.plus(expiryDuration);
            // Set time remaining
            //calculate the initial value of time remaining (if lower than 100 hours left)
            Duration timeLeft = Duration.between(now, expiryDate);
            organ.setTimeLeft(timeLeft);
            //Either the organ shouldn't be displaying, or it should display <4 days or something
        }
    }

    public void setup() {
        try {
            List<String> validCountries = new ArrayList<>();
            for(Country c : WindowManager.getDataManager().getGeneral().getAllCountries(token)) {
                if(c.getValid())
                    validCountries.add(c.getCountryName());
            }
            countryComboBox.setItems(FXCollections.observableArrayList(validCountries));
            countryComboBox.getItems().add("All Countries");
        } catch (HttpResponseException e) {
            Debugger.error("Could not populate combobox of countries. Failed to retrieve information from the server.");
        }

        List<String> nzRegions = new ArrayList<>();
        for(NZRegion r : NZRegion.values()) {
            nzRegions.add(r.toString());
        }
        regionComboBox.setItems(FXCollections.observableArrayList(nzRegions));
        regionComboBox.getItems().add("All Regions");


        countryComboBox.setValue("All Countries");
        setRegionControls("", "All Countries", regionComboBox, regionTextField);
    }

    /**
     * Gets the region from the regionComboBox or regionField. If New Zealand is the selected country in the
     * given countryComboBox, then the value in the regionComboBox is returned, otherwise the value in
     * the regionField is returned.
     *
     * @param countryComboBox The ComboBox of countries
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     * @return the value in the regionComboBox if New Zealand is the selected country, otherwise the value in the regionField.
     */
    public String getRegion(ComboBox<Country> countryComboBox, ComboBox<String> regionComboBox, TextField regionField) {
        boolean getFromComboBox = Objects.equals(countryComboBox.getValue(), "New Zealand");
        if(getFromComboBox) {
            return regionComboBox.getValue();
        }
        return regionField.getText();
    }


    /**
     * Sets the current value of the given regionComboBox and regionField to the given value.
     *
     * @param value The value which the ComboBox and TextField will be set to
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     */
    public void setRegion(String value, ComboBox countryComboBox, ComboBox<String> regionComboBox, TextField regionField) {
        String country = countryComboBox.getValue().toString();
        boolean useCombo = false;
        if (country != null) {
            useCombo = country.equalsIgnoreCase("New Zealand");
        }
        if(value == null) {
            if(useCombo){
                regionComboBox.getSelectionModel().select("All Regions");
            } else {
                regionField.setText("");
            }
        } else {
            if(useCombo){
                regionComboBox.getSelectionModel().select(value);
            } else {
                regionField.setText(value);
            }
        }
    }



    /**
     * Sets the visibility of the given regionComboBox and regionField depending on the value of the given
     * countryComboBox and userValue.
     * If New Zealand is selected in the countryComboBox, the  regionComboBox is visible, otherwise the regionField is visible.
     *
     * @param userValue The region value of the user (Could be region, or regionOfDeath)
     * @param country The country the user is from
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     */
    public void setRegionControls(String userValue, String country, ComboBox<String> regionComboBox, TextField regionField) {
        boolean useCombo = false;
        if (country != null) {
            useCombo = country.equalsIgnoreCase("New Zealand");
        }
        regionComboBox.setVisible(useCombo);
        regionField.setVisible(!useCombo);
        boolean validNZRegion;
        try {
            validNZRegion = Arrays.asList(NZRegion.values()).contains(NZRegion.parse(userValue));
        } catch (IllegalArgumentException e) {
            validNZRegion = false;
        }
        if((useCombo && validNZRegion) || (!useCombo && !validNZRegion)) {
            setRegion(userValue,countryComboBox, regionComboBox, regionField);
        } else {
            setRegion("", countryComboBox, regionComboBox, regionField);
        }
    }

    /**
     * Updates the visibility of the region controls and updates the undo stack if changes were made
     */
    public void countryChanged() {
        String currentRegion = getRegion(countryComboBox, regionComboBox, regionTextField);
        setRegionControls(currentRegion, countryComboBox.getValue().toString(), regionComboBox, regionTextField);
        filterChanged();
    }



    /**
     * Creates a timer which ticks every second and updates each organ object, counting down their expiry time by 1 second.
     * This timer runs in a background thread, and with only 1 timer running SHOULD be real time reliable.
     */
    private void initTimer(){

        // get data from server and load them into the tree table or whatever

        //set up the timer
        int delay = 1000;
        int period = 1000;
        Debugger.log("Initializing timer...");
        tick = new TimerTask() {
            public void run() {
                for (DonatableOrgan organ : expiryList){
                    if (organ.getTimeLeft().compareTo(Duration.ZERO) > 0){
                        //for each item set timeLeft -1
                        organ.tickTimeLeft();
                    } else { //Organ has hit 0 so should be removed
                        autoRefresh = true;
                    }
                }
                if (autoRefresh){
                    refreshTable();
                }
                organsTreeTable.refresh();
                autoRefresh = false;
            }

        };
        time.scheduleAtFixedRate(tick, delay, period);

    }
    /**
     * Updates the organs in the available organs table
     */
    public void updateOrgans() {
        try {
            HashMap filterParams = new HashMap();

            if(!countryComboBox.getValue().toString().equals("All Countries")) {
                filterParams.put("country", countryComboBox.getValue().toString());
            }

            String region = getRegion(countryComboBox, regionComboBox, regionTextField);
            if (!region.equals(allRegions) && !region.equals("")){
                filterParams.put("userRegion", region);
            }
            if (!organFilter.getSelectionModel().getSelectedItem().equals(allOrgans)){
                filterParams.put("organ", organFilter.getSelectionModel().getSelectedItem());
            }



            filterParams.put("receiverName", receiverNameTextField.getText());
            System.out.println(filterParams);
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(filterParams, token));
            setInitTimeLeft(temp);

            expiryList.clear();
            User lastUser = null;
            for(DonatableOrgan organ : temp) {
                if (!organ.getTimeLeft().isNegative() && !organ.getTimeLeft().isZero()) {
                    if(lastUser == null){
                        lastUser = addUserInfo(organ);
                    }
                    //if multiple organs from the same user no need to do multiple API calls
                    if (organ.getDonorId() == lastUser.getId()){
                        organ.setReceiverName(lastUser.getName());
                        organ.setReceiverDeathRegion(lastUser.getRegionOfDeath());
                    } else {
                        lastUser = addUserInfo(organ);
                    }
                    expiryList.add(organ);
                }
            }
            expiryList.sort(Comparator.comparing(DonatableOrgan::getTimeLeft));

            for (DonatableOrgan don : expiryList) {
                Debugger.log("DON: " + don.getTimeLeft());
            }

            Debugger.log("there are " + expiryList.size() + " items in expiryList when updating organs");
            TreeItem<Object> root = new TreeItem<>();
            for (DonatableOrgan organ : expiryList) {
                TreeItem expiringOrganItem = new TreeItem<>(organ);
                for (User receiver : organ.getTopReceivers()) {
                    expiringOrganItem.getChildren().add(new TreeItem<>(receiver));
                }
                root.getChildren().add(expiringOrganItem);
            }

            organColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof DonatableOrgan) {
                            return new ReadOnlyStringWrapper(((DonatableOrgan) param.getValue().getValue()).getOrganType().toString());
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );

            nameColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof User) {
                            return new ReadOnlyStringWrapper(((User) param.getValue().getValue()).getName());
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );

            countdownColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("timeLeft"));

            dateOfDeathColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof DonatableOrgan) {
                            return new ReadOnlyStringWrapper(((DonatableOrgan) param.getValue().getValue()).getTimeOfDeath().format(User.dateTimeFormat));
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );

            organRegionColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof DonatableOrgan) {
                            return new ReadOnlyStringWrapper(((DonatableOrgan) param.getValue().getValue()).getReceiverDeathRegion());
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );

            receiverRegionColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof User) {
                            return new ReadOnlyStringWrapper(((User) param.getValue().getValue()).getRegion());
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );




            organsTreeTable.setRoot(root);
            updated = true;
        } catch (HttpResponseException e) {
            e.printStackTrace();
            Debugger.error("Failed to update organs table...");
        }
    }



    /**
     * adds the user info to a Donatable organ item
     * @param organ the waiting list item to update
     * @return the retrieved user object to reduce API calls
     */
    private User addUserInfo(DonatableOrgan organ) {
        try{
            User user = WindowManager.getDataManager().getUsers().getUser(organ.getDonorId(), token);
            organ.setReceiverName(user.getName());
            organ.setReceiverDeathRegion(user.getRegionOfDeath());
            return user;
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve user with ID: " + organ.getDonorId());
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void filterApplied(){
        this.regionApplied = getRegion(countryComboBox, regionComboBox, regionTextField);
        this.organApplied = organFilter.getValue();
        this.nameApplied = receiverNameTextField.getText();
    }

    private void filterChanged(){
        String regionFilter =  getRegion(countryComboBox, regionComboBox, regionTextField);
        if(!this.receiverNameTextField.getText().equals(nameApplied) ||
                !this.organFilter.getValue().equals(organApplied) ||
                !regionFilter.equals(regionApplied)) {
            updateResultsLabel.setText("Click to apply filter changes");
            updateResultsLabel.setVisible(true);
        }
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        organsTreeTable.setShowRoot(false);

        WindowManager.setClinicianAvailableOrgansController(this);

        organsTreeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);


        ObservableList<String> organSearchlist = FXCollections.observableArrayList();
        Organ[] organsList = Organ.values();
        organSearchlist.add(allOrgans);
        for (Organ o : organsList) {
            String v = o.toString();
            organSearchlist.add(v);
        }
        organFilter.setItems(organSearchlist);
        organFilter.setValue(allOrgans);

        ObservableList<String> regionSearchlist = FXCollections.observableArrayList();
        NZRegion[] regionList = NZRegion.values();
        regionSearchlist.add(allRegions);
        for (NZRegion o : regionList) {
            String v = o.toString();
            regionSearchlist.add(v);
        }


        regionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });

        regionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });

        organFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });

        receiverNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });



        countdownColumn.setCellFactory(param -> new TreeTableCell<DonatableOrgan, Duration>() {

            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Long millis = item.toMillis();
                    setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)));

                }
            }
        });

        organsTreeTable.setRowFactory(new Callback<TreeTableView<Object>, TreeTableRow<Object>>() {
            @Override
            public TreeTableRow<Object> call(TreeTableView<Object> tableView) {
                final TreeTableRow<Object> row = new TreeTableRow<Object>() {
                    public void updateItem(Object item, boolean empty) {
                        getStyleClass().remove("highlighted-row-organs-25");
                        getStyleClass().remove("highlighted-row-organs-50");
                        getStyleClass().remove("highlighted-row-organs-75");
                        getStyleClass().remove("highlighted-row-organs-100");
                        super.updateItem(item, empty);
                        if(item instanceof DonatableOrgan) {
                            DonatableOrgan di = (DonatableOrgan) item;
                            setTooltip(null);
                            if (di.getTimePercent() <  0.25) {
                                if (!getStyleClass().contains("highlighted-row-organs-25")) {
                                    getStyleClass().add("highlighted-row-organs-25");
                                }
                            } else if (di.getTimePercent() <  0.50) {
                                if (!getStyleClass().contains("highlighted-row-organs-50")) {
                                    getStyleClass().add("highlighted-row-organs-50");
                                }
                            } else if (di.getTimePercent() <  0.75) {
                                if (!getStyleClass().contains("highlighted-row-organs-75")) {
                                    getStyleClass().add("highlighted-row-organs-75");
                                }
                            } else {
                                if (!getStyleClass().contains("highlighted-row-organs-100")) {
                                    getStyleClass().add("highlighted-row-organs-100");
                                }
                            }
                        }
                    }
                };
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2 && row.getItem() instanceof User) {
                        int receiverId = (int)((User) row.getItem()).getId();
                        try {
                            WindowManager.newAdminsUserWindow(WindowManager.getDataManager().getUsers().getUser(receiverId, token), token);
                        } catch (HttpResponseException e) {
                            Debugger.error("Could not open user window. Failed to fetch user with id: " + receiverId);
                        }
                    } else if(!row.isEmpty() && event.getClickCount() == 2 && row.getItem() instanceof DonatableOrgan) {
                        Debugger.log("Clicked organ row");
                        int donorId = (int)((DonatableOrgan) row.getItem()).getDonorId();
                        try {
                            WindowManager.newAdminsUserWindow(WindowManager.getDataManager().getUsers().getUser(donorId, token), token);
                        } catch (HttpResponseException e) {
                            Debugger.error("Could not open user window. Failed to fetch user with id: " + donorId);
                        }
                    }
                });
                organsTreeTable.refresh();
                return row;
            }
        });
        focused = false;
        fade.setNode(refreshSuccessText);
        fade.setFromValue(1.0);
        fade.setToValue(0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
    }

    /**
     * Starts the timer when the organs tab is first opened.
     */
    public void startTimer() {
        if (!focused) {
            initTimer();
        }
        focused = true;
    }

    /**
     * Stops the timer when the tab or window is exited.
     */
    public void stopTimer(){
        if(focused) {
            if (time != null) {
                tick.cancel();
            }
        }
        focused = false;
    }

    /**
     * Refreshes the available organs table and displays a label on a successful update.
     * Helps reduce server load by removing the need to create an auto update.
     */
    public void refreshTable(){
        updated = false;
        WindowManager.updateAvailableOrgans();
        if (updated){
            updateResultsLabel.setVisible(false);
            refreshSuccessText.setText("Updated successfully.");
            refreshSuccessText.setVisible(true);
            fade.playFromStart();
            filterApplied();
        }
    }

    /**
     * Called when an organ passes the colour threshold eg 50% to 49% and so needs to be updated.
     * Not using this calls updateOrgans() in the middle of the for loop (not ideal as throws concurrent mod. exceptions)
     * @param value the value of autoRefresh
     */
    public void setAutoRefresh(boolean value){
        autoRefresh = value;
    }
}
