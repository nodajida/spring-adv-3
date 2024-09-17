package com.sparta.adv3.domain.auth.controller;

import com.sparta.adv3.domain.auth.dto.SigninRequest;
import com.sparta.adv3.domain.auth.dto.SigninResponse;
import com.sparta.adv3.domain.auth.dto.SignupRequest;
import com.sparta.adv3.domain.auth.dto.SignupResponse;
import com.sparta.adv3.domain.auth.service.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원가입: signup
    //회원가입은 새로운 사용자가 프로그램에 등록하는 과정이에요. 이 부분에서 사용자는 이름, 이메일, 비밀번호 같은 정보를 보내면,
    // 프로그램은 그 정보를 받아서 회원으로 등록해요.
    @PutMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest){
        return authService.signup(signupRequest);
    }
    //로그인: signin
    @PutMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest){
        return authService.signin(signinRequest);
    }

}
//요약하면
//회원가입: 사용자가 이름, 이메일, 비밀번호를 보내면, 프로그램이 그 사람을 새로운 회원으로 등록해요.
//로그인: 이미 가입한 사용자가 이메일과 비밀번호를 보내면, 프로그램은 로그인을 시켜줘요.
//이 코드는 사용자들이 회원으로 가입하고 로그인할 수 있게 도와주는 역할을 해요!