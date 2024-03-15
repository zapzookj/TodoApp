package com.zapzook.todoapp.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class CommentRepositoryQueryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    private CommentRepositoryQueryImpl commentRepositoryQuery;

    private User user;
    private Todo todo;

    @BeforeEach
    public void setUp() {
        EntityManager em = entityManager.getEntityManager();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        commentRepositoryQuery = new CommentRepositoryQueryImpl(em);

        user = new User("testname", "password", "test@email.com");
        entityManager.persist(user);

        todo = new Todo("Test title", "Test contents", true, user);
        entityManager.persist(todo);

        Comment comment1 = new Comment("contents 1", todo, user);
        Comment comment2 = new Comment("contents 2", todo, user);
        entityManager.persist(comment1);
        entityManager.persist(comment2);
    }

    @Test
    void findByTodoId() {
        // given
        PageRequest pageRequest = PageRequest.of(0,5);

        // when
        Page<Comment> result = commentRepositoryQuery.findByTodoId(todo.getId(), pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }
}