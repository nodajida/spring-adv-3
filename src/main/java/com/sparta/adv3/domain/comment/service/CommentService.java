package com.sparta.adv3.domain.comment.service;

import com.sparta.adv3.domain.comment.dto.CommentResponse;
import com.sparta.adv3.domain.comment.dto.CommentSaveRequest;
import com.sparta.adv3.domain.comment.dto.CommentSaveResponse;
import com.sparta.adv3.domain.comment.entity.Comment;
import com.sparta.adv3.domain.comment.repository.CommentRepository;
import com.sparta.adv3.domain.common.dto.AuthUser;
import com.sparta.adv3.domain.common.exception.InvalidRequestException;
import com.sparta.adv3.domain.todo.entity.Todo;
import com.sparta.adv3.domain.todo.repository.TodoRepository;
import com.sparta.adv3.domain.user.dto.UserResponse;
import com.sparta.adv3.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CommentService {
    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentSaveResponse saveComment(AuthUser authUser, long todoId, CommentSaveRequest commentSaveRequest) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId).orElseThrow(() ->
                new InvalidRequestException("Todo not found"));
        Comment newComment = new Comment(
                commentSaveRequest.getContents(),
                user,
                todo
        );
        Comment savedComment = commentRepository.save(newComment);
        return new CommentSaveResponse(
                savedComment.getId(),
                savedComment.getContents(),
                new UserResponse(user.getId(), user.getEmail())
        );
    }
    public List<CommentResponse> getComments(long todoId) {
        List<Comment> commentList = commentRepository.findByTodoIdWithUser(todoId);
        List<CommentResponse> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = comment.getUser();
            CommentResponse dto = new CommentResponse(
                    comment.getId(),
                    comment.getContents(),
                    new UserResponse(user.getId(), user.getEmail())
            );

            dtoList.add(dto);
        }

        return dtoList;
    }
}