package seng302.Logic.Database;

import javafx.util.Pair;
import org.apache.commons.dbutils.DbUtils;
import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import seng302.Model.Message;
import seng302.Model.User;

import java.sql.*;
import java.util.*;

public class Conversations {
    /**
     * Gets all of the conversations for a specific user.
     *
     * @param id The id of the user to fetch information from
     * @param profileType The type of user
     * @return A list of the user's conversations
     * @throws SQLException If database interaction fails
     */
    public List<Conversation> getAllConversations(int id, ProfileType profileType) throws SQLException {
        List<Conversation> conversations = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT conversation_id FROM CONVERSATION_MEMBER WHERE user_id = ? AND access_level = ?");
            statement.setInt(1, id);
            statement.setInt(2, profileType.getAccessLevel());
            resultSet = statement.executeQuery();

            conversations = new ArrayList<>();
            while (resultSet.next()) {
                conversations.add(getSingleConversation(resultSet.getInt(1)));
            }
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }
        if (conversations == null) {
            throw error;
        } else {
            return conversations;
        }
    }

    /**
     * Gets a single conversation from the database.
     *
     * @param conversationId The id of the conversation to fetch
     * @return The conversation
     * @throws SQLException If database interaction fails
     */
    public Conversation getSingleConversation(int conversationId) throws SQLException {
        Conversation conversation = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM MESSAGE WHERE conversation_id = ? ORDER BY MESSAGE.id;");
            statement.setInt(1, conversationId);
            resultSet = statement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (resultSet.next()) {
                messages.add(new Message(
                        resultSet.getInt("id"),
                        resultSet.getString("text"),
                        resultSet.getTimestamp("date_time").toLocalDateTime(),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("access_level")));
            }
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);

            statement = connection.prepareStatement("SELECT user_id, access_level FROM CONVERSATION_MEMBER WHERE conversation_id = ?;");
            statement.setInt(1, conversationId);
            resultSet = statement.executeQuery();
            List<Pair<Integer, ProfileType>> participants = new ArrayList<>();
            while (resultSet.next()) {
                participants.add(new Pair<>(resultSet.getInt(1), ProfileType.fromAccessLevel(resultSet.getInt(2))));
            }
            conversation = new Conversation(conversationId, messages, participants);
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }

        if (conversation == null) {
            throw error;
        } else {
            return conversation;
        }
    }

    /**
     * Gets all of the users that are participating a conversation.
     *
     * @param conversationId The id of the conversation to check
     * @return A list of the id numbers of users in the conversation
     * @throws SQLException If database communication fails
     */
    public List<Integer> getConversationUsers(int conversationId) throws SQLException {
        List<Integer> userIds = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("SELECT user_id FROM CONVERSATION_MEMBER WHERE conversation_id = ?;");
            statement.setInt(1, conversationId);
            resultSet = statement.executeQuery();
            userIds = new ArrayList<>();
            while (resultSet.next()) {
                userIds.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }

        if (userIds == null) {
            throw error;
        } else {
            return userIds;
        }
    }

    /**
     * Adds a user to a conversation.
     *
     * @param id The id of the user to add
     * @param profileType The type of user
     */
    public void addConversationUser(int id, ProfileType profileType, int conversationId) throws SQLException {
        PreparedStatement statement = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("INSERT INTO CONVERSATION_MEMBER VALUES(?, ?, ?);");
            statement.setInt(1, conversationId);
            statement.setInt(2, id);
            statement.setInt(3, profileType.getAccessLevel());
            statement.execute();
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(statement);
        }
        if (error != null) {
            throw error;
        }
    }

    /**
     * Adds a message to a conversation.
     *
     * @param conversationId The id of the conversation to add to
     * @param message The message to add
     * @throws SQLException If database interaction fails
     */
    public void addMessage(int conversationId, Message message) throws SQLException {
        PreparedStatement statement = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("INSERT INTO MESSAGE(conversation_id, text, date_time, user_id, access_level) VALUES(?, ?, ?, ?, ?);");
            statement.setInt(1, conversationId);
            statement.setString(2, message.getText());
            statement.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
            statement.setInt(4, message.getUserId());
            statement.setInt(5, message.getAccessLevel());
            statement.execute();
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(statement);
        }
        if (error != null) {
            throw error;
        }

    }

    /**
     * Adds a new conversation to the database.
     *
     * @param participants The ids and access levels of all participants
     * @throws SQLException If database interaction fails
     */
    public int addConversation(List<Pair<Integer, ProfileType>> participants) throws SQLException {
        int id = -1;
        SQLException error = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("INSERT INTO CONVERSATION VALUES(0, ?)");
            String token = UUID.randomUUID().toString();
            statement.setString(1, token);
            statement.execute();
            DbUtils.closeQuietly(statement);
            statement = connection.prepareStatement("SELECT id FROM CONVERSATION WHERE token = ?");
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);

            for (Pair<Integer, ProfileType> participant: participants) {
                try {
                    statement = connection.prepareStatement("INSERT INTO CONVERSATION_MEMBER VALUES(?, ?, ?);");
                    statement.setInt(1, id);
                    statement.setInt(2, participant.getKey());
                    statement.setInt(3, participant.getValue().getAccessLevel());
                    statement.execute();
                } catch (SQLIntegrityConstraintViolationException ignored) {
                } finally {
                    DbUtils.closeQuietly(statement);
                }
            }
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(statement);
        }
        if (error != null) {
            throw error;
        }
        return id;
    }

    /**
     * Deletes a conversation from the database.
     *
     * @param conversationId The id of the conversation to delete
     * @throws SQLException If database interaction fails
     */
    public void removeConversation(int conversationId) throws SQLException {
        PreparedStatement statement = null;
        SQLException error = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {

            statement = connection.prepareStatement("DELETE FROM CONVERSATION WHERE id = ?;");
            //This will cascade delete all associated messages and members
            statement.setInt(1, conversationId);
            statement.execute();
        } catch (SQLException e) {
            error = e;
        } finally {
            DbUtils.closeQuietly(statement);
        }
        if (error != null) {
            throw error;
        }
    }

}
