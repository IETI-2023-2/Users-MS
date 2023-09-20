package edu.escuelaing.service.impl;

import edu.escuelaing.entity.User;
import edu.escuelaing.service.UserService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private Map<String, User> userMap = new HashMap<>();

    @Override
    public User createUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User updateUser(String userId, User updatedUser) {
        if (userMap.containsKey(userId)) {
            userMap.put(userId, updatedUser);
            return updatedUser;
        }
        return null;
    }

    @Override
    public void deleteUser(String userId) {
        userMap.remove(userId);
    }

}
