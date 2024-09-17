package com.sparta.adv3.domain.auth.service;

import com.sparta.adv3.confing.JwtUtil;
import com.sparta.adv3.confing.PasswordEncoder;
import com.sparta.adv3.domain.auth.dto.SigninRequest;
import com.sparta.adv3.domain.auth.dto.SigninResponse;
import com.sparta.adv3.domain.auth.dto.SignupRequest;
import com.sparta.adv3.domain.auth.dto.SignupResponse;
import com.sparta.adv3.domain.common.exception.InvalidRequestException;
import com.sparta.adv3.domain.user.entity.User;
import com.sparta.adv3.domain.user.enums.UserRole;
import com.sparta.adv3.domain.user.repository.UserRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Transactional
    //회원가입 처리: signup
    public SignupResponse signup(SignupRequest signupRequest) {

        //비밀번호 암호화
        //사용자가 입력한 비밀번호를 암호화해서 저장해요. 암호화는 비밀번호를 더 안전하게 만들기 위한 방법
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        //사용자 역할 확인
        //사용자가 어떤 역할을 가질지 결정해요. 예를 들어, "일반 사용자"인지, "관리자"인지 구분할 수 있어요.
        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        //이메일 중복 체크
        // 사용자가 입력한 이메일이 이미 사용 중인 이메일인지 확인해요. 만약 이미 사용 중인 이메일이면 오류를 던져요.
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        //새로운 사용자 만들기
        //사용자가 입력한 이메일과 암호화된 비밀번호를 사용해서 새로운 회원을 만들어요.
        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                userRole
        );

        //회원 저장하기
        //새로 만든 회원을 데이터베이스에 저장해요.
        User savedUser = userRepository.save(newUser);

        //토큰 만들기
        //사용자가 로그인할 수 있게 토큰을 만들어요. 토큰은 사용자를 구분하는 디지털 키라고 생각하면 돼요.
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        //응답 보내기
        //토큰을 만들어서 회원가입이 성공적으로 완료되었다는 응답을 보내요.
        return new SignupResponse(bearerToken);
    }

    //로그인 처리: signin
    public SigninResponse signin(SigninRequest signinRequest) throws AuthException {
        //사용자 찾기
        //입력한 이메일을 데이터베이스에서 찾는다 만약 그 이메일로 가입한 사용자가 없다면 오류를 던진다.
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저"));
        //비밀번호 확인
        //사용자가 입력한 비밀번호와 데이터베이스에 저장된 암호화된 비밀번호와 일치하는지 확인 만약 다르면 오류를 던진다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }
        //토큰 만들기
        //로그인에 성공하면 토큰을 만들어서 보낸다.
        String bearerToken = jwtUtil.createToken(user.getId(),user.getEmail(),user.getUserRole());
        //응답 보내기
        //로그인에 성공하면 사용자가 사용할 수 있는 토큰으로 응답을 보낸다.
        return new SigninResponse((bearerToken));
    }
    //정리
    //회원가입: 사용자가 이메일, 비밀번호를 입력하면 그걸 확인해서 새로운 회원으로 등록
    //로그인: 이미 회원으로 등록된 사용자가 이메일과 비밀번호를 입력하면 그걸 확인해서 로그인할 수 있도록 한다.
    //이 코드는 사용자가 회원가입 그리고 로그인을 할 수 있도록 하는 서비스
}
