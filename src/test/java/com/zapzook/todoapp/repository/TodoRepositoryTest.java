package com.zapzook.todoapp.repository;

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
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    @BeforeEach
    public void setUp() {

        user = new User("user", "password", "user@email.com");
        userRepository.save(user);

        Todo todo1 = new Todo();
        todo1.setTitle("Todo 1");
        todo1.setContents("Contents 1");
        todo1.setCompleted(false);
        todo1.setUser(user);

        Todo todo2 = new Todo();
        todo2.setTitle("Todo 2");
        todo2.setContents("Contents 2");
        todo2.setCompleted(true);
        todo2.setUser(user);

        todoRepository.save(todo1);
        todoRepository.save(todo2);
    }

    @Test
    @DisplayName("findAllByCompletedFalseOrderByCreatedAtDesc - 완료되지 않은 Todo 조회 성공")
    void findAllByCompletedFalseOrderByCreatedAtDescSuccess() {
        // when
        List<Todo> result = todoRepository.findAllByCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompleted()).isFalse();
    }

    @Test
    @DisplayName("findByTitleContaining - 제목에 특정 문자열 포함된 Todo 조회 성공")
    void findByTitleContainingSuccess() {
        // given
        String titleKeyword = "Todo";

        // when
        List<Todo> result = todoRepository.findByTitleContaining(titleKeyword);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(todo -> todo.getTitle().contains(titleKeyword));
    }

    @Test
    @DisplayName("findByTitleContaining - 제목에 특정 문자열 포함된 Todo 조회 실패 (문자열 불일치)")
    void findByTitleContainingFail() {
        // given
        String titleKeyword = "hehehehe";

        // when
        List<Todo> result = todoRepository.findByTitleContaining(titleKeyword);

        // then
        assertThat(result).isEmpty();
    }
}