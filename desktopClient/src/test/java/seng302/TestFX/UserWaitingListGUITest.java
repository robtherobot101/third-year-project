package seng302.TestFX;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserWaitingListGUITest extends TestFXTest {

    private User user;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() throws SQLException {
        try {
            WindowManager.getDataManager().getGeneral().reset(null);
        } catch (HttpResponseException e) {
            Debugger.error("Failed to reset the database.");
        }
        user = addTestUser();
    }

    /**
     * Logs into the test user's account and navigates to the transplant waiting list.
     * This method should only be run from the main login screen.
     * @throws TimeoutException catch time outs on requests
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
     * @throws TimeoutException catch time outs on requests
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
    public List<WaitingListItem> waitingListItems() {
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

    @Test
    public void donorCannotSeeTransplantWaitingListOption() {
        userWindow(user);
        assert (!lookup("#waitingListButton").query().isVisible());
    }


    public boolean transplantListHasItem(TableView<WaitingListItem> table, Organ organ, String receiverName){
        return getTransplantListItem(table,organ,receiverName)!=null;
    }

    public WaitingListItem getTransplantListItem(TableView<WaitingListItem> table, Organ organ, String receiverName){
        for(WaitingListItem item:table.getItems()){
            if(item.getReceiverName().equals(receiverName) && item.getOrganType()==organ){
                return item;
            }
        }
        return null;
    }

}

