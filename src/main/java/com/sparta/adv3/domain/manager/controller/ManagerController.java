package com.sparta.adv3.domain.manager.controller;

import com.sparta.adv3.confing.JwtUtil;
import com.sparta.adv3.domain.common.annotation.Auth;
import com.sparta.adv3.domain.common.dto.AuthUser;
import com.sparta.adv3.domain.manager.dto.ManagerResponse;
import com.sparta.adv3.domain.manager.dto.ManagerSaveRequest;
import com.sparta.adv3.domain.manager.dto.ManagerSaveResponse;
import com.sparta.adv3.domain.manager.service.ManagerService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final JwtUtil jwtUtil;
    //매니저 추가
    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ManagerSaveResponse> saveManager(
            @Auth AuthUser authUser,
            @PathVariable long todoId,
            @Valid @RequestBody ManagerSaveRequest managerSaveRequest
    ) {
        return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest));
    }
    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable long todoId) {
        return ResponseEntity.ok(managerService.getManagers(todoId));
    }
    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public void deleteManager(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable long todoId, // URL에서 todo 항목의 고유 ID를 가져오는 방법
            @PathVariable long managerId //URL에서 관리자의 고유 ID를 가져오는 방법
    ) {
        Claims claims = jwtUtil.extractClaims(bearerToken.substring(7));
        long userId = Long.parseLong(claims.getSubject());
        managerService.deleteManager(userId, todoId, managerId);
    }
}