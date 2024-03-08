package com.zapzook.todoapp.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.QTodo;
import com.zapzook.todoapp.entity.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TodoRepositoryQueryImpl {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory qf;

    public TodoRepositoryQueryImpl(EntityManager em) {
        this.qf = new JPAQueryFactory(em);
    }

    public Todo findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;

        return qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
    }
}
