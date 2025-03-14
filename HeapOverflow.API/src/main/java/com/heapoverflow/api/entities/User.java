package com.heapoverflow.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_google_id", length = 30)
    private String id;

    @Column(name = "user_name", nullable = false, unique = false)
    private String username;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    public User() {}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    // @Override
    // public String toString() {
    //     try {
    //         ObjectMapper objectMapper = new ObjectMapper();
    //         return objectMapper.writeValueAsString(this);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return "{}";
    //     }
    // }
}