package seng302.Logic.Database;

import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import seng302.Model.Message;

import java.sql.SQLException;
import java.util.List;

public class Conversations {
    public List<Conversation> getAllConversations(int id, ProfileType profileType) throws SQLException {
        return null;
    }

    public Conversation getSingleConversation(int id, int conversationId, ProfileType profileType) throws SQLException {
        return null;
    }

    public void addMessage(int id, int conversationId, ProfileType profileType, Message message) throws SQLException {
    }

    public void addConversation(int id, ProfileType profileType, Message message) throws SQLException {
    }

    public void removeConversation(int id, int conversationId, ProfileType profileType, Message message) throws SQLException {
    }
}
