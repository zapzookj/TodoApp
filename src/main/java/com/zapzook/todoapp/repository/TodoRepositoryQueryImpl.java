package com.zapzook.todoapp.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.zapzook.todoapp.entity.QTodo.todo;

@Repository
public class TodoRepositoryQueryImpl {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory qf;

    public TodoRepositoryQueryImpl(EntityManager em) {
        this.qf = new JPAQueryFactory(em);
    }

    public Todo findByIdWithUser(Long todoId) {
        return qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
    }

    public List<Todo> findAllWithUser(String username) {
        return qf
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.user.username.eq(username))
                .fetch();
    }

    public Page<Todo> findAllByUserName(String username, Pageable pageable) {
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

        var totalSize = qf
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
