package com.sparta.adv3.domain.todo.controller;

import com.sparta.adv3.domain.common.annotation.Auth;
import com.sparta.adv3.domain.common.dto.AuthUser;
import com.sparta.adv3.domain.todo.dto.TodoResponse;
import com.sparta.adv3.domain.todo.dto.TodoSaveRequest;
import com.sparta.adv3.domain.todo.dto.TodoSaveResponse;
import com.sparta.adv3.domain.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // 게시물 생성
    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse>saveTodo(@Auth AuthUser authUser, @Valid @RequestBody TodoSaveRequest todoSaveRequest){
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    //모든 할 일의 목록을 가져옵니다.
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size));
    }
    //특정 할 일을 가져온다
    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }
}