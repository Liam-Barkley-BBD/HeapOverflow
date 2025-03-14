package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String userGoogleId) {
        return userRepository.findById(userGoogleId);
    }

    public List<User> getUsersByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    public List<User> getUsersByEmail(String email) {
        return userRepository.findByEmailContaining(email);
    }

    public boolean userExistsById(String userId) {
        return userRepository.existsById(userId);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
