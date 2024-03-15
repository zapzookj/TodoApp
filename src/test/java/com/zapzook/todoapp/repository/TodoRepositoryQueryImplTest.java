package com.zapzook.todoapp.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryQueryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    private TodoRepositoryQueryImpl todoRepositoryQuery;

    private User user;

    @BeforeEach
    public void setUp() {
        EntityManager em = entityManager.getEntityManager();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        todoRepositoryQuery = new TodoRepositoryQueryImpl(em);

        user = new User("testname", "password", "test@email.com");
        entityManager.persist(user);

        Todo todo1 = new Todo("Test title", "Test contents", true, user);
        Todo todo2 = new Todo("Test title 2", "Test contents 2", false, user);
        entityManager.persist(todo1);
        entityManager.persist(todo2);
    }

    @Test
    @DisplayName("findTodoWithUserById - Id로 할일 카드 조회")
    void findTodoWithUserByIdTest() {
        // given
        User user1 = new User("username", "password", "email@email.com");
        entityManager.persist(user1);

        Todo todo1 = new Todo("Test title", "Test contents", true, user);
        entityManager.persist(todo1);

        // when
        Todo findTodo = todoRepositoryQuery.findTodoWithUserById(todo1.getId());

        // then
        assertThat(findTodo).isNotNull();
    }

    @Test
    @DisplayName("findAllTodosByUser - 자신이 작성한 할일 카드 조회")
    void findAllTodosByUserTest() {
        // given
        User otherUser = new User("otheruser", "otherpass", "other@email.com");
        entityManager.persist(otherUser);
        Todo otherTodo = new Todo("other", "other", true, otherUser);
        entityManager.persist(otherTodo);

        // when
        List<Todo> myTodos = todoRepositoryQuery.findAllTodosByUser(user.getUsername());

        // then
        assertThat(myTodos).hasSize(2); // 다른 사람의 할일 카드는 조회되지 않음
    }

    @Test
    @DisplayName("findAllTodosVisibleToUser - 조회 가능한 모든 할일 카드 조회")
    void findAllTodosVisibleToUserTest() {
        // given
        User otherUser = new User("otheruser", "otherpass", "other@email.com");
        entityManager.persist(otherUser);
        Todo otherTodo1 = new Todo("other", "other", true, otherUser);
        Todo otherTodo2 = new Todo("other2", "other2", false, otherUser);
        entityManager.persist(otherTodo1);
        entityManager.persist(otherTodo2);
        PageRequest pageRequest = PageRequest.of(0,5);

        // when
        Page<Todo> result = todoRepositoryQuery.findAllTodosVisibleToUser(user.getUsername(), pageRequest);

        // then
        assertThat(result.getContent()).hasSize(3); // 비공개 상태인 다른 사람의 할일 카드는 조회되지 않음
    }

    @Test
    @DisplayName("searchTodosByTitle - 키워드가 제목에 포함된, 조회 가능한 모든 할일 카드 조회")
    void searchTodosByTitleTest() {
        // given
        String param = "Test";
        PageRequest pageRequest = PageRequest.of(0,5);

        // when
        Page<Todo> result = todoRepositoryQuery.searchTodosByTitle(param, user.getUsername(), pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
    }
}