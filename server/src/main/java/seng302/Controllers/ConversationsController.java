package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.Conversations;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import seng302.Model.Message;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @return The serialised conversation
     */
    public String getSingleConversation(Request request, Response response) {
        Conversation queriedConversation;
        int requestedConversationId = Integer.parseInt(request.params(":conversationId"));
        try {
            queriedConversation = model.getSingleConversation(requestedConversationId);
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
     * Adds a user to a conversation.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @param profileType The type of user accessing the conversations
     * @return Whether the operation succeeded
     */
    public String addConversationUser(Request request, Response response, ProfileType profileType) {
        return null;
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
        int userId = Integer.parseInt(request.params(":id"));
        int conversationId = Integer.parseInt(request.params(":conversationId"));

        if (request.body() == null || request.body().isEmpty()) {
            Server.getInstance().log.warn("Empty request body");
            response.status(400);
            return "Missing message body";
        } else {
            try {
                int accessLevel = 0;
                if (profileType == ProfileType.CLINICIAN) {
                    accessLevel = 1;
                } else if (profileType == ProfileType.ADMIN) {
                    accessLevel = 2;
                }
                model.addMessage(conversationId, new Message(request.body(), userId, accessLevel));
                response.status(201);
                return "Success";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    /**
     * A formatter class to streamline deserialization of conversation participant input.
     */
    private class conversationUserListFormat {
        Map<ProfileType, List<Integer>> participants;
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
        Gson gson = new Gson();
        Map<ProfileType, List<Integer>> conversationParticipants;
        int userId = Integer.parseInt(request.params(":id"));

        // Attempt to parse received JSON
        try {
            conversationParticipants = gson.fromJson(request.body(), conversationUserListFormat.class).participants;
            if (conversationParticipants == null) {
                Server.getInstance().log.warn("Empty request body");
                response.status(400);
                return "Missing participant information";
            }
            for (ProfileType type: ProfileType.values()) {
                conversationParticipants.computeIfAbsent(type, k -> new ArrayList<>());
            }
            //Add self to conversation if not already there
            if (!conversationParticipants.get(profileType).contains(userId)) {
                conversationParticipants.get(profileType).add(userId);
            }
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }

        try {
            model.addConversation(conversationParticipants);
            response.status(201);
            return "Success";
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
    }
    /**
     * Deletes a user-clinician conversation.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether the operation succeeded
     */
    public String removeConversation(Request request, Response response) {
        int conversationId = Integer.parseInt(request.params(":conversationId"));

        try {
            model.removeConversation(conversationId);
            response.status(200);
            return "Success";
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
    }
}
