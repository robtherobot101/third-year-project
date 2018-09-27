package seng302.Logic.Database;

import javafx.util.Pair;
import org.apache.commons.dbutils.DbUtils;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Clinician;
import seng302.Model.Conversation;
import seng302.Model.Message;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ConversationsTest extends GenericTest {

    private Conversations conversations = new Conversations();

    @Test
    public void addConversation() throws SQLException {
        GeneralClinician generalClinician = new GeneralClinician();
        HelperMethods.insertClinician(generalClinician);
        Clinician converser = generalClinician.getAllClinicians().get(0);

        List<Integer> participants = new ArrayList<>();
        participants.add((int)converser.getStaffID());

        conversations.addConversation(participants);
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean nextConversation, nextMember;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM CONVERSATION");
            resultSet = statement.executeQuery();
            nextConversation = resultSet.next();
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
            statement = connection.prepareStatement("SELECT * FROM CONVERSATION_MEMBER");
            resultSet = statement.executeQuery();
            nextMember = resultSet.next();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        assertTrue(nextConversation && nextMember);
    }

    @Test
    public void addConversationUser() throws SQLException {
        GeneralClinician generalClinician = new GeneralClinician();
        HelperMethods.insertClinician(generalClinician);
        Clinician converser = generalClinician.getAllClinicians().get(0);

        List<Integer> participants = new ArrayList<>();
        participants.add((int)converser.getStaffID());

        int convoId = conversations.addConversation(participants);
        conversations.addConversationUser(1, ProfileType.ADMIN, convoId);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean nextMember;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM CONVERSATION_MEMBER WHERE conversation_id = ?");
            statement.setInt(1, convoId);
            resultSet = statement.executeQuery();
            nextMember = resultSet.next() && resultSet.next();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        assertTrue(nextMember);
    }



    @Test
    public void removeConversation() throws SQLException {
        GeneralClinician generalClinician = new GeneralClinician();
        HelperMethods.insertClinician(generalClinician);
        Clinician converser = generalClinician.getAllClinicians().get(0);

        List<Integer> participants = new ArrayList<>();
        participants.add((int)converser.getStaffID());

        conversations.addConversation(participants);
        List<Conversation> databaseConversations = conversations.getAllConversations((int)converser.getStaffID(), ProfileType.CLINICIAN);
        for (Conversation conversation: databaseConversations) {
            conversations.removeConversation(conversation.getId());
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean nextConversation, nextMember;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM CONVERSATION");
            resultSet = statement.executeQuery();
            nextConversation = resultSet.next();
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
            statement = connection.prepareStatement("SELECT * FROM CONVERSATION_MEMBER");
            resultSet = statement.executeQuery();
            nextMember = resultSet.next();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        assertTrue(!nextConversation && !nextMember);
    }


    @Test
    public void addMessage() throws SQLException {
        GeneralClinician generalClinician = new GeneralClinician();
        HelperMethods.insertClinician(generalClinician);
        Clinician converser = generalClinician.getAllClinicians().get(0);

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add(new Message(Integer.toString(i), (int)converser.getStaffID(), 0));
        }
        List<Integer> participants = new ArrayList<>();
        participants.add((int)converser.getStaffID());
        int id = conversations.addConversation(participants);
        for (Message message: messages) {
            conversations.addMessage(id, message);
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT COUNT(*) FROM MESSAGE WHERE conversation_id = ? GROUP BY conversation_id;");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            resultSet.next();
            count = resultSet.getInt(1);
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        assertEquals(messages.size(), count);
    }

    @Test
    public void getSingleConversation() throws SQLException, IOException {
        new Administration().reset();
        GeneralClinician generalClinician = new GeneralClinician();
        HelperMethods.insertClinician(generalClinician);
        Clinician converser = generalClinician.getAllClinicians().get(0);

        List<Integer> participants = new ArrayList<>();
        participants.add((int)converser.getStaffID());
        int id = conversations.addConversation(participants);
        Conversation conversation = conversations.getSingleConversation(id);
        assertEquals(participants, conversation.getMembers());
    }
}
