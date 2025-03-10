package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByUsername(String username);

//    List<User> findAllUsers();
}

