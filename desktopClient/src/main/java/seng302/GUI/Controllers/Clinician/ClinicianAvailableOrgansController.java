package seng302.GUI.Controllers.Clinician;

import javafx.animation.FadeTransition;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.NZRegion;
import seng302.User.Attribute.Organ;
import seng302.User.DonatableOrgan;
import seng302.User.User;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class ClinicianAvailableOrgansController implements Initializable{

    @FXML
    AnchorPane organsPane;

    @FXML
    TreeTableColumn<Object, String> organColumn;
    @FXML
    TreeTableColumn<Object, String> nameColumn;
    @FXML
    TreeTableColumn<Object, String> countdownColumn;
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
    Label updateResultsLabel;

    @FXML
    Button refreshOrganTable;

    @FXML
    ComboBox<String> organFilter;
    @FXML
    ComboBox<String> regionFilter;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private Timer time = new Timer();
    private String token;
    private boolean focused = false;
    private boolean updated;
    private FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(3500));

    private String organApplied, regionApplied, nameApplied = "";


    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null;
    }


    List<DonatableOrgan> expiryList = new ArrayList<>();

    /**
     * Sets the initial time left values for all list items.
     */
    public void setInitTimeLeft(List<DonatableOrgan> expiryList){

        for (DonatableOrgan organ : expiryList){
            //for each item in the list
            LocalDateTime now = LocalDateTime.now();

            // Calculate the date the organ expires, based on the organ
            LocalDateTime deathDate = organ.getTimeOfDeath();
            //TODO Find a cleaner way of getting organ dates and times in durations or something to be able to add to deathDate, and find expiry durations for ear and tissue
            Duration expiryDuration = organ.getExpiryDuration(organ.getOrganType());
            LocalDateTime expiryDate = deathDate.plus(expiryDuration);

            if (now.isBefore(expiryDate) && expiryDate.isBefore(now.plusHours(100))){
                // Set time remaining
                //calculate the initial value of time remaining (if lower than 100 hours left)
                Duration timeLeft = Duration.between(now, expiryDate);
                organ.setTimeLeft(timeLeft);
            } else {
                Duration timeLeft = Duration.between(now, expiryDate);
                organ.setTimeLeft(timeLeft);
                //Either the organ shouldn't be displaying, or it should display <4 days or something
            }

        }
        //create timer task to tick down


        //TODO figure out how to handle changing tab - end the timer or leave it running in the background until app close??
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
        time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            public void run() {
            for (DonatableOrgan organ : expiryList){
                if (organ.getTimeLeft().compareTo(Duration.ZERO) > 0){
                    organ.tickTimeLeft();
                }
                organsTreeTable.refresh();
            }


            }

        }, delay, period);

    }
    /**
     * Updates the organs in the available organs table
     */
    public void updateOrgans() {
        try {
            HashMap filterParams = new HashMap();
            if (regionFilter.getSelectionModel().getSelectedItem() != "All Regions"){
                filterParams.put("userRegion", regionFilter.getSelectionModel().getSelectedItem());
            }
            if (organFilter.getSelectionModel().getSelectedItem() != "All Organs"){
                filterParams.put("organ", organFilter.getSelectionModel().getSelectedItem());
            }
            filterParams.put("receiverName", receiverNameTextField.getText());
            System.out.println(filterParams);
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(filterParams, token));
            setInitTimeLeft(temp);

            expiryList.clear();
            for (DonatableOrgan organ : temp) {
                if (!organ.getTimeLeft().isNegative() && !organ.getTimeLeft().isZero()) {
                    addUserInfo(organ);
                    expiryList.add(organ);
                }
            }
            expiryList.sort(Comparator.comparing(DonatableOrgan::getTimeLeft));

            for (DonatableOrgan don : expiryList) {
                Debugger.log("DON: " + don.getTimeLeftString());
            }

            Debugger.log("there are " + expiryList.size() + " items in expiryList when updating organs");
            TreeItem<Object> root = new TreeItem<>();
            for (DonatableOrgan organ : expiryList) {
                TreeItem expiringOrganItem = new TreeItem<>(organ);
                for (int receiverId : organ.getTopReceivers()) {
                    User receiver = WindowManager.getDataManager().getUsers().getUser(receiverId, token);
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

            countdownColumn.setCellValueFactory(
                    param -> {
                        if (param.getValue().getValue() instanceof DonatableOrgan) {
                            return new ReadOnlyStringWrapper(((DonatableOrgan) param.getValue().getValue()).getTimeLeftString());
                        } else {
                            return new ReadOnlyStringWrapper("");
                        }
                    }
            );

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
        } catch (HttpResponseException e) {
            e.printStackTrace();
            Debugger.error("Failed to update organs table...");
        }
    }



    /**
     * adds the user info to a Donatable organ item
     * @param organ the waiting list item to update
     */
    private void addUserInfo(DonatableOrgan organ) {
        try{
            User user = WindowManager.getDataManager().getUsers().getUser(organ.getDonorId(), token);
            organ.setReceiverName(user.getName());
            organ.setReceiverDeathRegion(user.getRegionOfDeath());
        } catch (HttpResponseException | NullPointerException e) {
            Debugger.error("Failed to retrieve user with ID: " + organ.getDonorId());
        }
    }

    private void filterApplied(){
        this.regionApplied = regionFilter.getValue();
        this.organApplied = organFilter.getValue();
        this.nameApplied = receiverNameTextField.getText();
    }

    private void filterChanged(){
        if(!this.receiverNameTextField.getText().equals(nameApplied) ||
                !this.organFilter.getValue().equals(organApplied) ||
                !this.regionFilter.getValue().equals(regionApplied)) {
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
        organSearchlist.add("All Organs");
        for (Organ o : organsList) {
            String v = o.toString();
            organSearchlist.add(v);
        }
        organFilter.setItems(organSearchlist);
        organFilter.setValue("All Organs");

        ObservableList<String> regionSearchlist = FXCollections.observableArrayList();
        NZRegion[] regionList = NZRegion.values();
        regionSearchlist.add("All Regions");
        for (NZRegion o : regionList) {
            String v = o.toString();
            regionSearchlist.add(v);
        }
        regionFilter.setItems(regionSearchlist);
        regionFilter.setValue("All Regions");




        regionFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });

        organFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
        });

        receiverNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterChanged();
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
    }

    public void startTimer() {
        if (!focused) {
            initTimer();
        }
        focused = true;
    }

    public void stopTimer(){
        if (focused &&time != null) {
            time.cancel();
            time.purge();
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
        updateResultsLabel.setText("Updated successfully.");
        updateResultsLabel.setVisible(true);

        if (updated){
            fade.playFromStart();

            filterApplied();
        }
    }
}
