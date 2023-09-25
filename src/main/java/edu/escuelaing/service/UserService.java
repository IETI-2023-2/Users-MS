package edu.escuelaing.service;

import edu.escuelaing.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(String userId);

    List<User> getAllUsers();

    Optional<User> updateUser(String userId, User updatedUser);

    void deleteUser(String userId);

    String getPasswordByUsername(String username);

    String getRoleByUsername(String username);

}