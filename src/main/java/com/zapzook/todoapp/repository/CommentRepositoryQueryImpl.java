package com.zapzook.todoapp.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.QComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryQueryImpl {
    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory qf;

    public CommentRepositoryQueryImpl(EntityManager em) {
        this.qf = new JPAQueryFactory(em);
    }

    public List<Comment> findByTodoId(Long todoId) {
        QComment comment = QComment.comment;

        return qf
                .selectFrom(comment)
                .leftJoin(comment.todo).fetchJoin()
                .where(comment.todo.id.eq(todoId))
                .fetch();
    }
}
