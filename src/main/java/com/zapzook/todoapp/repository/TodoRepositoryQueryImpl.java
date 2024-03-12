package com.zapzook.todoapp.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.QTodo;
import com.zapzook.todoapp.entity.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

    public Page<Todo> findAllByUserName(String username, Pageable pageable) {
        QTodo todo = QTodo.todo;

        var query = qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.completed.eq(false)
                        .and(todo.open.eq(true)
                                .or(todo.user.username.eq(username))));

        query.orderBy(todo.createdAt.desc());

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var todos = query.fetch();

        long totalSize = qf
                .select(Wildcard.count)
                .from(todo)
                .leftJoin(todo.user)
                .where(todo.completed.eq(false)
                        .and(todo.open.eq(true)
                                .or(todo.user.username.eq(username))))
                .fetch().get(0);

        return PageableExecutionUtils.getPage(todos, pageable, () -> totalSize);
    }

    public Page<Todo> findAllByParamAndUserName(String param, String username, Pageable pageable) {
        QTodo todo = QTodo.todo;

        var query = qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(
                        todo.title.contains(param)
                                .and(todo.open.isTrue()
                                        .and(todo.completed.isFalse()
                                                .or(todo.user.username.eq(username)))));
        query.orderBy(todo.createdAt.desc());

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var todos = query.fetch();

        long totalSize = qf
                .select(Wildcard.count)
                .from(todo)
                .leftJoin(todo.user)
                .where(
                        todo.title.contains(param)
                                .and(todo.open.isTrue().
                                        and(todo.completed.isFalse().
                                                or(todo.user.username.eq(username)))))
                .fetch().get(0);

        return PageableExecutionUtils.getPage(todos, pageable, () -> totalSize);
    }
}
