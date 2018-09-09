package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.Conversations;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.List;

public class ConversationsController {
    private Conversations model;

    /**
     * Constructs a new ConversationsController
     */
    public ConversationsController() {
        model = new Conversations();
    }

    /**
     * Gets all of a user or clinician's conversations and returns them as a serialised json string.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return The serialised conversations
     */
    public String getAllConversations(Request request, Response response, ProfileType profileType) {
        List<Conversation> queriedConversations;
        int requestedUserId = Integer.parseInt(request.params(":id"));
        try {
            queriedConversations = model.getAllConversations(requestedUserId, profileType);
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedConversations = gson.toJson(queriedConversations);

        response.type("application/json");
        response.status(200);
        return serialQueriedConversations;
    }

    /**
     * Gets a specific user or clinician's conversation and returns it as a serialised json string.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return The serialised conversation
     */
    public String getSingleConversation(Request request, Response response, ProfileType profileType) {
        Conversation queriedConversation;
        int requestedUserId = Integer.parseInt(request.params(":id"));
        int requestedConversationId = Integer.parseInt(request.params(":conversationId"));
        try {
            queriedConversation = model.getSingleConversation(requestedUserId, requestedConversationId, profileType);
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedConversation = gson.toJson(queriedConversation);

        response.type("application/json");
        response.status(200);
        return serialQueriedConversation;
    }

    /**
     * Adds a message to a conversation.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return Whether the operation succeeded
     */
    public String addMessage(Request request, Response response, ProfileType profileType) {
        return null;
    }

    /**
     * Adds a new user-clinician conversation.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return Whether the operation succeeded
     */
    public String addConversation(Request request, Response response, ProfileType profileType) {
        return null;
    }

    /**
     * Deletes a user-clinician conversation.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return Whether the operation succeeded
     */
    public String removeConversation(Request request, Response response, ProfileType profileType) {
        return null;
    }
}
