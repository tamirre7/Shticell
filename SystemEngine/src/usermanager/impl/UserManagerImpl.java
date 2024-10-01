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

    @Override
    public synchronized void addUser(String username) {usersSet.add(username);}

    @Override
    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }
    @Override
    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }
    @Override
    public boolean isUserExists(String username) {return usersSet.contains(username);}
}
