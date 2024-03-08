package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.ApiUseTime;
import com.zapzook.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {

    Optional<ApiUseTime> findByUser(User loginuser);

}
