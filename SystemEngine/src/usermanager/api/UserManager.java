package usermanager.api;

import java.util.Set;

public interface UserManager {
    void addUser(String username);
    void removeUser(String username);
    Set<String> getUsers();
    boolean isUserExists(String username);
}
