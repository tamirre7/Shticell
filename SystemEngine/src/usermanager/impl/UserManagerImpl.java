package usermanager.impl;

import usermanager.api.UserManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManagerImpl implements UserManager {
    private final Set<String> usersSet;

    public UserManagerImpl() {
        usersSet = new HashSet<>();
    }

    // Adds a user to the set.
    // This method is synchronized to ensure thread-safe access.
    @Override
    public synchronized void addUser(String username) {
        usersSet.add(username);
    }

    // Removes a user from the set.
    // This method is synchronized to ensure thread-safe access.
    @Override
    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    // Returns an unmodifiable view of the users set.
    // This method is synchronized to ensure thread-safe access.
    @Override
    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    // Checks if a user exists in the set, case-insensitively.
    @Override
    public boolean isUserExists(String username) {
        for (String user : usersSet) {
            if (user.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}
