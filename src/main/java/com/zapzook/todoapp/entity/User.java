package com.zapzook.todoapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String introduce;

    @Column
    private String profileImage;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(Long userId, String username, String email, String profileImage, String introduce) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.password = "password";
        this.profileImage = profileImage;
        this.introduce = introduce;
    }

    public void update(String introduce, String profileImage) {
        this.introduce = introduce;
        this.profileImage = profileImage;
    }
}
