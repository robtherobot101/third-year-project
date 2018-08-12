package seng302.Model;

import java.util.List;

public class UserCSVStorer {

    private List<User> storedUsers;

    public UserCSVStorer(List<User> userList) {
        this.storedUsers = userList;
    }

    public List<User> getUsers() {
        return this.storedUsers;
    }
}
