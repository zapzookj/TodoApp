package com.zapzook.todoapp.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import static com.zapzook.todoapp.entity.QComment.comment;

@Repository
public class CommentRepositoryQueryImpl {
    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory qf;

    public CommentRepositoryQueryImpl(EntityManager em) {
        this.qf = new JPAQueryFactory(em);
    }

    public Page<Comment> findByTodoId(Long todoId, Pageable pageable) {
        var query = qf
                .selectFrom(comment)
                .leftJoin(comment.todo).fetchJoin()
                .where(comment.todo.id.eq(todoId));

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var comments = query.fetch();

        long totalSize = qf
                .select(Wildcard.count)
                .from(comment)
                .leftJoin(comment.todo)
                .where(comment.todo.id.eq(todoId))
                .fetch().get(0);

        return PageableExecutionUtils.getPage(comments, pageable, () -> totalSize);
    }
}
