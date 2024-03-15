package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.repository.TodoRepositoryQueryImpl;
import com.zapzook.todoapp.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoRepositoryQueryImpl todoRepositoryQuery;

    @Mock
    private Util util;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private TodoRequestDto requestDto;
    private Todo todo;
    private List<Todo> todoList = new ArrayList<>();
    private Page<Todo> page;

    @BeforeEach
    void setUp() {
        user = new User("testname", "testpassword", "test@email.com");
        requestDto = new TodoRequestDto("title", "contents", true);
        todo = new Todo("title", "contents", false, user);
    }

    void todoListSetup() {
        Todo todo1 = new Todo("Test1 title", "Test1 contents", true, user);
        Todo todo2 = new Todo("Test2 title", "Test2 contents", true, user);

        this.todoList.add(todo);
        this.todoList.add(todo1);
        this.todoList.add(todo2);

        this.page = new PageImpl<>(todoList);
    }

    @Test
    @DisplayName("할일 카드 등록")
    void createTodoTest() {
        //when
        todoService.createTodo(requestDto, user);

        //then
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    @DisplayName("특정 할일 카드 조회")
    void getTodoTestSuccess() {
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
    @DisplayName("특정 할일 카드 조회 - 실패(비공개 상태)")
    void getTodoTestFail1() {
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
    void getTodoTestFail2() {
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
    @DisplayName("본인이 작성한 할일 카드 조회")
    void getMyTodosTest() {
        // given
        todoListSetup();
        given(todoRepositoryQuery.findAllTodosByUser(user.getUsername())).willReturn(todoList);

        // when
        List<TodoResponseDto> result = todoService.getMyTodos(user.getUsername());

        // then
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("할일 카드 전체 조회")
    void getTodoListTest() {
        // given
        todoListSetup();
        int pageNum = 0;
        int size = 5;
        String sortBy = "title";
        boolean isAsc = true;
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        given(todoRepositoryQuery.findAllTodosVisibleToUser(user.getUsername(), pageable)).willReturn(page);

        // when
        Page<TodoResponseDto> result = todoService.getTodoList(user.getUsername(), pageNum, size, sortBy, isAsc);

        // then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("할일 카드 검색")
    void searchTodosTest() {
        // given
        todoListSetup();
        String param = "param";
        int pageNum = 0;
        int size = 5;
        String sortBy = "title";
        boolean isAsc = true;
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        given(todoRepositoryQuery.searchTodosByTitle(param, user.getUsername(), pageable)).willReturn(page);

        // when
        Page<TodoResponseDto> result = todoService.searchTodos(param, user.getUsername(), pageNum, size, sortBy, isAsc);

        // then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("할일 카드 수정")
    void updateTodoTest() {
        // given
        Long todoId = 1L;
        TodoRequestDto updateDto = new TodoRequestDto("Update Title", "Update Contents", true);
        given(util.findTodo(todoId, user)).willReturn(todo);

        // when
        todoService.updateTodo(todoId, updateDto, user);

        // then
        assertEquals(updateDto.getTitle(), todo.getTitle());
        assertEquals(updateDto.getContents(), todo.getContents());
    }

    @Test
    @DisplayName("할일 카드 완료 처리")
    void test8(){
        // given
        given(util.findTodo(todo.getId(), user)).willReturn(todo);

        // when
        todoService.completeTodo(todo.getId(), user);

        // assert
        assertEquals(true, todo.getCompleted());
    }
}