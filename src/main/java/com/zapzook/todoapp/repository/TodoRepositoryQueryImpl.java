package com.zapzook.todoapp.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.QTodo;
import com.zapzook.todoapp.entity.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Todo> findAllByUserName(String username) {
        QTodo todo = QTodo.todo;

        return qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.completed.eq(false).and(todo.open.eq(true).or(todo.user.username.eq(username))))
                .orderBy(todo.createdAt.desc())
                .fetch();
    }

    public List<Todo> findAllByParamAndUserName(String param, String username) {
        QTodo todo = QTodo.todo;

        return qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.title.contains(param).and(todo.open.isTrue().and(todo.completed.isFalse().or(todo.user.username.eq(username)))))
                .orderBy(todo.createdAt.desc())
                .fetch();
    }
}
