package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, String> {

    Boolean existsByEmail(String email);
    
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByEmailContaining(String email, Pageable pageable);

    Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable);
}
