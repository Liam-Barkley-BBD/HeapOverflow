package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> getUserById(String userGoogleId) {
        return userRepository.findById(userGoogleId);
    }

    public Page<User> getUsersByFilter(String username, String email, Pageable pageable) {
        if (username != null && email != null) {
            return userRepository.findByUsernameContainingAndEmailContaining(username, email, pageable);
        } else if (username != null) {
            return userRepository.findByUsernameContaining(username, pageable);
        } else if (email != null) {
            return userRepository.findByEmailContaining(email, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public boolean userExistsById(String userId) {
        return userRepository.existsById(userId);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new UserAlreadyExistsException("User already exists");
        } 
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User email already exists");
        }
        return userRepository.save(user);
    }

}
