package com.sparta.adv3.domain.comment.service;

import com.sparta.adv3.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentAdminService {
    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}