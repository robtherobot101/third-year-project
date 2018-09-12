package seng302.Logic.Database;

import org.apache.commons.dbutils.DbUtils;
import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import seng302.Model.Message;
import seng302.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversations {
    public List<Conversation> getAllConversations(int id, ProfileType profileType) throws SQLException {
        List<Conversation> conversations = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                statement = connection.prepareStatement("SELECT conversation_id FROM CONVERSATION_MEMBER WHERE user_id = ? AND access_level = ?");
                statement.setInt(1, id);
                int accessLevel = 0;
                if (profileType == ProfileType.CLINICIAN) {
                    accessLevel = 1;
                } else if (profileType == ProfileType.ADMIN) {
                    accessLevel = 2;
                }
                statement.setInt(2, accessLevel);
                resultSet = statement.executeQuery();

                conversations = new ArrayList<>();
                while (resultSet.next()) {
                    conversations.add(getSingleConversation(resultSet.getInt(1)));
                }
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            } finally {
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);
            }
        }
        if (conversations == null) {
            throw new SQLException("Unable to fetch conversations");
        } else {
            return conversations;
        }
    }

    public Conversation getSingleConversation(int conversationId) throws SQLException {
        Conversation conversation = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
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
                Map<ProfileType, List<Integer>> participants = new HashMap<>();
                for (ProfileType profileType: ProfileType.values()) {
                    participants.put(profileType, new ArrayList<>());
                }
                while (resultSet.next()) {
                    switch (resultSet.getInt(2)) {
                        case 0:
                            participants.get(ProfileType.USER).add(resultSet.getInt(1));
                            break;
                        case 1:
                            participants.get(ProfileType.CLINICIAN).add(resultSet.getInt(1));
                            break;
                        case 2:
                            participants.get(ProfileType.ADMIN).add(resultSet.getInt(1));
                    }
                }
                conversation = new Conversation(conversationId, messages, participants);
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            } finally {
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);
            }
        }
        if (conversation == null) {
            throw new SQLException("Unable to fetch conversation");
        } else {
            return conversation;
        }
    }

    public void addMessage(int conversationId, Message message) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("INSERT INTO MESSAGE VALUES(0, ?, ?, ?, ?, ?);");
                statement.setInt(1, conversationId);
                statement.setString(2, message.getText());
                statement.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
                statement.setInt(4, message.getUserId());
                statement.setInt(5, message.getAccessLevel());
                statement.execute();
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }

    public void addConversation(Map<ProfileType, List<Integer>> participants) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("INSERT INTO CONVERSATION VALUES(0);" +
                        "SELECT LAST_INSERT_ID();");
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int id = resultSet.getInt(1);
                DbUtils.closeQuietly(statement);
                DbUtils.closeQuietly(resultSet);
                for (ProfileType profileType: ProfileType.values()) {
                    int accessLevel = 0;
                    switch (profileType) {
                        case CLINICIAN:
                            accessLevel = 1;
                            break;
                        case ADMIN:
                            accessLevel = 2;
                    }
                    for (int userId: participants.get(profileType)) {
                        statement = connection.prepareStatement("INSERT INTO CONVERSATION_MEMBER VALUES(?, ?, ?);");
                        statement.setInt(1, id);
                        statement.setInt(2, userId);
                        statement.setInt(3, accessLevel);
                        statement.execute();
                        DbUtils.closeQuietly(statement);
                    }
                }
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }

    public void removeConversation(int conversationId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("DELETE FROM CONVERSATION WHERE id = ?;");
                statement.setInt(1, conversationId);
                statement.execute();
                DbUtils.closeQuietly(statement);
                statement = connection.prepareStatement("DELETE FROM CONVERSATION_MEMBER WHERE conversation_id = ?;");
                statement.setInt(1, conversationId);
                statement.execute();
                DbUtils.closeQuietly(statement);
                statement = connection.prepareStatement("DELETE FROM MESSAGE WHERE conversation_id = ?;");
                statement.setInt(1, conversationId);
                statement.execute();
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }
}
