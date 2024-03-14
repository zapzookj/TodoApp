package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.introduce = :introduce, u.profileImage = :profileImage WHERE u.id = :id")
    void updateProfile(@Param("id") Long id, @Param("introduce") String introduce, @Param("profileImage") String profileImage);
}
