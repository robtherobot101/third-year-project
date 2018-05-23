package seng302.TestFX;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.GUI.Controllers.UserWindowController;
import seng302.GUI.TFScene;
import seng302.Generic.ReceiverWaitingListItem;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.User;

public class UserWaitingListGUITest extends TestFXTest {

    private User user;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() {
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

    @Test
    public void itemAddedToWaitingList_appearsInWaitingList() throws TimeoutException {
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        assert (user.getWaitingListItems().get(0).getOrganType().equals(Organ.LIVER));
        assert (waitingListItems().get(0).getOrganType().equals(Organ.LIVER));
    }

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
        assertEquals(1, waitingListItems().size());
    }

    @Test
    public void registerDonatingOrgan_organIsHighlightedInWaitingList() throws TimeoutException {
        user.getOrgans().add(Organ.LIVER);
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        assertTrue(getWaitingListOrgan(Organ.LIVER).getStyleClass().contains("highlighted-row"));
    }

    @Test
    public void deregisterDonatingOrgan_organIsNotHighlightedInWaitingList() throws TimeoutException {
        user.getOrgans().add(Organ.LIVER);
        usersTransplantWaitingListAsClinician();
        register(Organ.LIVER);
        deregister(Organ.LIVER);
        assertFalse(getWaitingListOrgan(Organ.LIVER).getStyleClass().contains("highlighted-row"));
    }

}

