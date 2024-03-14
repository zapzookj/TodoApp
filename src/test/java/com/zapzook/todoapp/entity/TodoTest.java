package com.zapzook.todoapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

    @Test
    @DisplayName("Todo 생성 테스트")
    void createTodoTest() {
        // given
        User user = new User("testname", "password", "email@email.com");
        String title = "title";
        String contents = "contents";
        boolean open = true;

        // when
        Todo todo = new Todo(title, contents, open, user);

        // then
        assertNotNull(todo);
        assertEquals("title", todo.getTitle());
        assertEquals("contents", todo.getContents());
        assertEquals(true, todo.getOpen());
    }

    @Test
    @DisplayName("Todo update 테스트")
    void update() {
        // given
        User user = new User("testname", "password", "email@email.com");
        Todo todo = new Todo("title", "contents", true, user);

        // when
        todo.update("update", "update");

        // then
        assertEquals("update", todo.getTitle());
        assertEquals("update", todo.getContents());
    }

    @Test
    @DisplayName("Todo complete 테스트")
    void complete() {
        // given
        User user = new User("testname", "password", "email@email.com");
        Todo todo = new Todo("title", "contents", true, user);

        // when
        todo.complete();

        // then
        assertEquals(true, todo.getCompleted());
    }
}