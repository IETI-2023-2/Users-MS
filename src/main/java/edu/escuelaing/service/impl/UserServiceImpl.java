package edu.escuelaing.service.impl;

import edu.escuelaing.entity.User;
import edu.escuelaing.repository.UserRepository;
import edu.escuelaing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> updateUser(String userId, User updatedUser) {
        return userRepository.findById(userId).map(existingUser -> {
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            return userRepository.save(existingUser);
        });
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
