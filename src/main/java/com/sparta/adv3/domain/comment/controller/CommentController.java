package com.sparta.adv3.domain.comment.controller;

import com.sparta.adv3.domain.comment.dto.CommentResponse;
import com.sparta.adv3.domain.comment.dto.CommentSaveRequest;
import com.sparta.adv3.domain.comment.dto.CommentSaveResponse;
import com.sparta.adv3.domain.comment.service.CommentService;
import com.sparta.adv3.domain.common.annotation.Auth;
import com.sparta.adv3.domain.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    //댓글 생성
    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
            @Auth AuthUser authUser,
            @PathVariable long todoId,
            @Valid @RequestBody CommentSaveRequest commentSaveRequest
    ) {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequest));
    }
    //댓글 목록을 조회
    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable long todoId) {
        return ResponseEntity.ok(commentService.getComments(todoId));
    }
}
