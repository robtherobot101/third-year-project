package seng302.Model;

import javafx.util.Pair;
import seng302.Model.Attribute.ProfileType;

import java.util.List;

public class Conversation {
    private int id;
    private List<Message> messages;
    private List<Pair<Integer, ProfileType>> members;

    /**
     * Constructs a new conversation from a database entry.
     *
     * @param id The id of the conversation
     * @param messages The messages in the conversation
     * @param members The conversation members
     */
    public Conversation(int id, List<Message> messages, List<Pair<Integer, ProfileType>> members) {
        this.id = id;
        this.messages = messages;
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Pair<Integer, ProfileType>> getMembers() {
        return members;
    }

    public int getId() {
        return id;
    }
}
