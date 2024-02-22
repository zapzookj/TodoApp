package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private Todo savedTodo;
    private User savedUser;

    @BeforeEach
    public void setUp() {

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEmail("test@email.com");
        savedUser = userRepository.save(user);

        Todo todo = new Todo();
        todo.setTitle("todo");
        todo.setContents("contents");
        todo.setOpen(true);
        todo.setUser(savedUser);
        savedTodo = todoRepository.save(todo);


        Comment comment = new Comment();
        comment.setContents("contents");
        comment.setUser(savedUser);
        comment.setTodo(savedTodo);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("findByTodoId - 성공")
    void findByTodoIdSuccess() {
        // given
        Long todoId = savedTodo.getId();

        // when
        List<Comment> foundComments = commentRepository.findByTodoId(todoId);

        // then
        assertThat(foundComments).isNotEmpty();
        assertThat(foundComments.get(0).getContents()).isEqualTo("Test Comment");
        assertThat(foundComments.get(0).getUser()).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("findByTodoId - 실패 (todoId 존재 X)")
    void findByTodoIdFail() {
        // given
        Long todoId = 999L;

        // when
        List<Comment> foundComments = commentRepository.findByTodoId(todoId);

        // then
        assertThat(foundComments).isEmpty();
    }
}