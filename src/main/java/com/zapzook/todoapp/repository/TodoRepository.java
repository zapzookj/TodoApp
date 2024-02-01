package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByCompletedFalseOrderByCreatedAtDesc();
}
