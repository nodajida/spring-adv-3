package com.sparta.adv3.domain.user.service;

import com.sparta.adv3.confing.PasswordEncoder;
import com.sparta.adv3.domain.common.exception.InvalidRequestException;
import com.sparta.adv3.domain.user.dto.UserChangePasswordRequest;
import com.sparta.adv3.domain.user.dto.UserResponse;
import com.sparta.adv3.domain.user.entity.User;
import com.sparta.adv3.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    //userRepository는 사용자 정보를 저장하고 찾아주는 도구야. 마치 학교에서 도서관 사서가 책을 찾아주고 기록해두는 것처럼,
    // 이 도구는 데이터베이스에서 사용자의 정보를 찾아주거나 저장해줘.
    private final UserRepository userRepository;
    //passwordEncoder는 비밀번호를 암호화해주는 도구야. 비밀번호를 그대로 저장하면 위험하니까,
    // 이 도구를 사용해서 비밀번호를 복잡한 암호로 바꿔 저장해. 마치 비밀 일기에 글을 쓸 때 비밀 암호를 사용하는 것처럼!
    private final PasswordEncoder passwordEncoder;

    //public UserResponse getUser(long userId)
    //이 함수는 사용자의 정보를 가져오는 역할을 해. 마치 친구의 학급 정보를 알아내는 것처럼,
    // userId로 사용자를 찾아서 그 정보(아이디와 이메일)를 가져와.
    public UserResponse getUser(long userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        //이 부분은 데이터베이스에서 userId를 가진 사용자를 찾는 과정이야.
        //만약 그 사용자를 찾지 못하면, "사용자를 찾을 수 없습니다"라는 에러를 보여줘.
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        // return new UserResponse(user.getId(), user.getEmail());
        // 사용자를 찾으면, 사용자의 아이디와 이메일 정보를 담아서 돌려주는 거야. 친구의 이름과 학급을 확인한 후 그 정보를 돌려주는 것과 같아.
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {

        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {

            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }
}