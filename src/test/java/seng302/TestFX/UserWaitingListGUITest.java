package seng302.TestFX;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.matcher.control.TableViewMatchers;
import seng302.Generic.ReceiverWaitingListItem;
import seng302.Generic.TransplantWaitingListItem;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.Clinician;
import seng302.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class UserWaitingListGUITest extends TestFXTest {

    private User user;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        //defaultTestSetup();
    }

    @Before
    public void setUp() throws SQLException {
        WindowManager.getDatabase().resetDatabase();
        user = addTestUser();
    }

    /**
     * Logs into the test user's account and navigates to the transplant waiting list.
     * This method should only be run from the main login screen.
     */
    public void usersTransplantWaitingListAsUser() throws TimeoutException{
        userWindow(user);
        waitForNodeVisible(10,"#waitingListButton");
        clickOn("#waitingListButton");
    }

    /**
     * Logs into the default clinician account,
     * opens the test user's profile, and then navigates to the
     * transplant waiting list.
     * This method should only be run from the main login screen.
     */
    public void usersTransplantWaitingListAsClinician() throws TimeoutException {
        userWindowAsClinician(user);
        waitForNodeVisible(10,"#waitingListButton");
        clickOn("#waitingListButton");
    }

    /**
     * Registers an organ of the given type.
     * This method should only be run from the transplant waiting list view opened by a clinician.
     *
     * @param type The organ type to register
     */
    public void register(Organ type) {
        selectComboBoxOrgan(type);
        clickOn("#registerOrganButton");
    }

    /**
     * Deregisters an organ of the given type from the open transplant waiting list
     * This method should only be run from the transplant waiting list view opened by a clinician with
     * an organ of the given type already registered
     *
     * @param type The organ type to deregister
     */
    public void deregister(Organ type) {
        clickOn(getWaitingListOrgan(type));
        clickOn("#deregisterOrganButton");
        clickOn("OK");
    }

    /**
     * Changes the selection in the organTypeComboBox to the given organ type.
     * This method should only be run from the transplant waiting list view opened by a clinician.
     *
     * @param type The type of organ
     */
    public void selectComboBoxOrgan(Organ type) {
        ComboBox<Organ> organs = lookup("#organTypeComboBox").queryComboBox();
        clickOn(organs);
        for (int i = 0; i < organs.getItems().size(); i++) {
            type(KeyCode.UP);
        }

        for (int i = 0; i < organs.getItems().size(); i++) {
            if (organs.getSelectionModel().getSelectedItem() == type) {
                type(KeyCode.ENTER);
                break;
            }
            type(KeyCode.DOWN);
        }
    }

    /**
     * Returns the row in the transplant waiting list which has the given organ type.
     *
     * @param type The given organ type
     * @return The table row with the given organ type
     */
    public Node getWaitingListOrgan(Organ type) {
        return (Node) from(lookup("#waitingListTableView")).lookup(type.toString().toLowerCase()).query().getParent();
    }

    /**
     * Returns all the WaitingListItems from the transplant waiting list TableView
     *
     * @return The items in the transplant waiting list
     */
    public List<ReceiverWaitingListItem> waitingListItems() {
        ArrayList waitingListItems = new ArrayList<>();
        for (Object o : lookup("#waitingListTableView").queryTableView().getItems()) {
            waitingListItems.add(o);
        }
        return waitingListItems;
    }

    @Test
    public void clinicianCanUpdateTransplantWaitingList() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        assert (lookup("#registerOrganButton").query().isVisible());
        assert (lookup("#deregisterOrganButton").query().isVisible());
        assert (lookup("#organTypeComboBox").query().isVisible());
    }

    @Ignore
    @Test
    public void receiverCannotUpdateTransplantWaitingList() throws TimeoutException {
        user.getWaitingListItems().add(new ReceiverWaitingListItem(Organ.BONE,(long)-1));
        usersTransplantWaitingListAsUser();
        assert (!lookup("#registerOrganButton").query().isVisible());
        assert (!lookup("#deregisterOrganButton").query().isVisible());
        assert (!lookup("#organTypeComboBox").query().isVisible());
    }

    @Test
    public void donorCannotSeeTransplantWaitingListOption() {
        userWindow(user);
        assert (!lookup("#waitingListButton").query().isVisible());
    }

    @Ignore
    @Test
    public void itemAddedToWaitingList_appearsInWaitingList() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        assert (user.getWaitingListItems().get(0).getOrganType().equals(Organ.LIVER));
        assert (waitingListItems().get(0).getOrganType().equals(Organ.LIVER));
    }

    @Ignore
    @Test
    public void itemRegisteredIn_userBecomesReceiver() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.KIDNEY);
        assertTrue(user.isReceiver());
    }

    @Test
    public void noRegisteredItemsInWaitingList_userIsNotReceiver() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.KIDNEY);
        deregister(Organ.KIDNEY);
        assertFalse(user.isReceiver());
    }

    @Ignore
    @Test
    public void itemDeregistered_hasDeregisteredDateAndIsStillWaitingOnIsFalse() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        deregister(Organ.LIVER);
        assert (!waitingListItems().get(0).getStillWaitingOn());
        assert (waitingListItems().get(0).getOrganDeregisteredDate() != null);
    }

    @Test
    public void deregisteredItemReregistered_overridesDeregisteredItem() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.TISSUE);
        deregister(Organ.TISSUE);
        register(Organ.TISSUE);
        assertEquals(2, waitingListItems().size());
    }

    @Ignore
    @Test
    public void registerDonatingOrgan_organIsHighlightedInWaitingList() throws TimeoutException {
        user.getOrgans().add(Organ.LIVER);
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        assertTrue(getWaitingListOrgan(Organ.LIVER).getStyleClass().contains("highlighted-row"));
    }

    @Ignore
    @Test
    public void deregisterDonatingOrgan_organIsNotHighlightedInWaitingList() throws TimeoutException {
        user.getOrgans().add(Organ.LIVER);
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        deregister(Organ.LIVER);
        assertFalse(getWaitingListOrgan(Organ.LIVER).getStyleClass().contains("highlighted-row"));
    }


    public boolean transplantListHasItem(TableView<TransplantWaitingListItem> table, Organ organ, String receiverName){
        return getTransplantListItem(table,organ,receiverName)!=null;
    }

    public TransplantWaitingListItem getTransplantListItem(TableView<TransplantWaitingListItem> table, Organ organ, String receiverName){
        for(TransplantWaitingListItem item:table.getItems()){
            System.out.println("item name: "+item.getName());
            System.out.println("item organ: "+item.getOrganType());
            if(item.getName().equals(receiverName) && item.getOrganType()==organ){
                return item;
            }
        }
        return null;
    }

    public void pressDialogOKButtons(){
        sleep(500);
        for(Window window: this.listWindows()){
            for(Node nodeWithOKText : from(window.getScene().getRoot()).lookup("OK").queryAll()){
                if(nodeWithOKText instanceof Button){
                    clickOn(nodeWithOKText);
                }
            }
        }
    }


    @Test
    public void changeUserWaitingList_clinicianWaitingListUpdated() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.HEART);
        openClinicianWindow(new Clinician("test","test","test"));
        waitForNodeVisible(5,"#transplantListButton");
        clickOn("#transplantListButton");
        TableView transplantTable = lookup("#transplantTable").queryTableView();
        assertTrue(transplantListHasItem(transplantTable, Organ.HEART, user.getName()));
    }

    @Ignore
    @Test
    public void changeClinicianList_userWaitingListUpdated() throws TimeoutException {
        user.getWaitingListItems().add(new ReceiverWaitingListItem(Organ.HEART,0, user.getId()));
        openClinicianWindow(new Clinician("test","test","test"));
        waitForNodeVisible(5,"#transplantListButton");
        clickOn("#transplantListButton");
        TableView transplantTable = lookup("#transplantTable").queryTableView();
        transplantTable.getSelectionModel().select(getTransplantListItem(transplantTable,Organ.HEART,user.getName()));

        clickOn("#deregisterReceiverButton");
        pressDialogOKButtons();

        clickOn("#saveButton");

        pressDialogOKButtons();


        usersTransplantWaitingListAsClinician();
        sleep(500);

        System.out.println(waitingListItems().size());
        assertTrue(waitingListItems().size()==0);
    }
}

