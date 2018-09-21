package seng302.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Conversation {
    private int id;
    private List<Message> messages;
    private List<Integer> members;
    private Map<Integer, String> memberNames;

    /**
     * Constructs a new conversation from a database entry.
     *
     * @param id The id of the conversation
     * @param messages The messages in the conversation
     * @param memberNames The conversation members
     */
    public Conversation(int id, List<Message> messages, Map<Integer, String> memberNames) {
        this.id = id;
        this.messages = messages;
        this.memberNames = memberNames;
        members = new ArrayList<>(memberNames.keySet());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public Map<Integer, String> getMemberNames() {
        return memberNames;
    }

    public int getId() {
        return id;
    }
}
