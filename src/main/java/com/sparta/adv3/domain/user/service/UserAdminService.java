package com.sparta.adv3.domain.user.service;

import com.sparta.adv3.domain.common.exception.InvalidRequestException;
import com.sparta.adv3.domain.user.dto.UserRoleChangeRequest;
import com.sparta.adv3.domain.user.entity.User;
import com.sparta.adv3.domain.user.enums.UserRole;
import com.sparta.adv3.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {
    private final UserRepository userRepository;
    @Transactional
    public void changeUserRole(Long userId, UserRoleChangeRequest userRoleChangeRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
