package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Util util;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private TodoRequestDto requestDto;
    private Todo todo;

    @BeforeEach
    void setUp() {
        user = new User("testname", "testpassword", "test@email.com");
        requestDto = new TodoRequestDto("테스트 제목", "테스트 내용", true);
        todo = new Todo(requestDto, user);
    }

    @Test
    @DisplayName("할일 카드 등록")
    void test1(){
        //given
        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        //when
        TodoResponseDto result = todoService.createTodo(requestDto, user);

        //then
        assertEquals(requestDto.getTitle(), result.getTitle());
        assertEquals(requestDto.getContents(), result.getContents());
        assertTrue(result.getOpen());
    }

    @Test
    @DisplayName("특정 할일 카드 조회")
    void test2(){
        // given
        Long todoId = 1L;
        given(util.findTodo(todoId)).willReturn(todo);

        // when
        TodoResponseDto result = todoService.getTodo(todoId, user.getUsername());

        // then
        assertEquals(todo.getTitle(), result.getTitle());
        assertEquals(todo.getContents(), result.getContents());
        assertEquals(todo.getId(), result.getId());
        assertEquals(todo.getUser().getUsername(), result.getUsername());
        assertEquals(todo.getOpen(), result.getOpen());
    }
    @Test
    @DisplayName("특정 할일 카드 조회 - 실패(비공개 처리)")
    void test3(){
        // given
        todo.setId(1L);
        todo.setOpen(false);
        User other = new User("other", "other", "other@email.com");

        given(util.findTodo(todo.getId())).willReturn(todo);

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> todoService.getTodo(todo.getId(), other.getUsername()));
        assertEquals("비공개된 할일카드입니다. 작성자만 조회가 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특정 할일 카드 조회 - 실패(완료된 카드)")
    void test4(){
        // given
        todo.setId(1L);
        todo.setCompleted(true);

        given(util.findTodo(todo.getId())).willReturn(todo);

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> todoService.getTodo(todo.getId(), user.getUsername()));
        assertEquals("해당 할일카드는 완료되어 숨김처리 되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("할일 카드 전체 조회") // 본인이 작성한 비공개 할일 카드, 다른 사용자가 작성한 비공개 할일 카드가 존재함
    void test5(){
        // given
        todo.setOpen(false);
        User other = new User("other", "other", "other@email.com");
        Todo publicTodoByOther = new Todo(new TodoRequestDto("Other's public todo", "test", true), other);
        Todo privateTodoByOther = new Todo(new TodoRequestDto("Other's private todo", "test", false), other);

        List<Todo> todos = Arrays.asList(todo, publicTodoByOther, privateTodoByOther);

        given(todoRepository.findAllByCompletedFalseOrderByCreatedAtDesc()).willReturn(todos);

        // when
        List<TodoResponseDto> result = todoService.getTodoList(user.getUsername());

        // then
        assertNotNull(result);
        assertEquals(2, result.size()); // 다른 사용자가 작성한 비공개 카드는 포함되지 않아서, 2개 조회되어야 함
        assertTrue(result.stream().anyMatch(todo -> todo.getTitle().equals("테스트 제목"))); // 비공개 상태지만 본인이 작성한 할일 카드는 조회되어야함
    }

    @Test
    @DisplayName("할일 카드 수정 - 성공")
    void test6(){
        // given
        Long todoId = 1L;
        TodoRequestDto updateDto = new TodoRequestDto("수정된 제목", "수정된 내용", true);
        given(util.findTodo(todoId, user)).willReturn(todo);

        // when
        TodoResponseDto result = todoService.updateTodo(todoId, updateDto, user);

        // then
        assertEquals(updateDto.getTitle(), result.getTitle());
        assertEquals(updateDto.getContents(), result.getContents());
    }

    @Test
    @Disabled
    @DisplayName("할일 카드 수정 - 실패")
    void test7(){ // findTodo의 예외처리 로직을 여기서 검증하는건 힘들듯.. 통합 테스트에서 검증해보자
        // given
        todo.setId(1L);
        TodoRequestDto updateDto = new TodoRequestDto("수정된 제목", "수정된 내용", true);
        User other = new User("other", "other", "other@email.com");
        given(todoRepository.findById(todo.getId())).willReturn(Optional.of(todo));

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> todoService.updateTodo(todo.getId(), updateDto, other));
        assertEquals("작성자만 삭제/수정이 가능합니다.", exception.getMessage());
    }



}