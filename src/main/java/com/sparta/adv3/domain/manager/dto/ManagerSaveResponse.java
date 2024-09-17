package com.sparta.adv3.domain.manager.dto;

import com.sparta.adv3.domain.user.dto.UserResponse;
import lombok.Getter;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserResponse user;

    public ManagerSaveResponse(Long id, UserResponse user) {
        this.id = id;
        this.user = user;
    }
}

