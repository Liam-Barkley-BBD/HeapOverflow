package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(String userGoogleId);
    
    List<User> findByUsername(String username);

//    List<User> findAllUsers();
}

