package com.zapzook.todoapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Comment 생성 테스트")
    void createCommentTest() {
        // given
        User user = new User("testname", "password", "email@email.com");
        Todo todo = new Todo("title", "contents", true, user);
        String contents = "contents";

        // when
        Comment comment = new Comment(contents, todo, user);

        // then
        assertNotNull(comment);
        assertEquals("contents", comment.getContents());
    }

    @Test
    @DisplayName("Comment update 테스트")
    void update() {
        // given
        User user = new User("testname", "password", "email@email.com");
        Todo todo = new Todo("title", "contents", true, user);
        Comment comment = new Comment("contents", todo, user);

        // when
        comment.update("update");

        // then
        assertEquals("update", comment.getContents());
    }
}