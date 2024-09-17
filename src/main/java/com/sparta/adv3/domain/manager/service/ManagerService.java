package com.sparta.adv3.domain.manager.service;

import com.sparta.adv3.domain.common.dto.AuthUser;
import com.sparta.adv3.domain.common.exception.InvalidRequestException;
import com.sparta.adv3.domain.manager.dto.ManagerResponse;
import com.sparta.adv3.domain.manager.dto.ManagerSaveRequest;
import com.sparta.adv3.domain.manager.dto.ManagerSaveResponse;
import com.sparta.adv3.domain.manager.entity.Manager;
import com.sparta.adv3.domain.manager.repository.ManagerRepository;
import com.sparta.adv3.domain.todo.entity.Todo;
import com.sparta.adv3.domain.todo.repository.TodoRepository;
import com.sparta.adv3.domain.user.dto.UserResponse;
import com.sparta.adv3.domain.user.entity.User;
import com.sparta.adv3.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
//이 서비스는 어떤 사람이 일정을 관리할 수 있도록 도와주고, 담당자를 추가하거나 삭제하는 기능을 제공
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        if (!ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다!");
        }
        // 새로운 담당자 찾기
        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다!"));

        //작성자가 자기 자신을 담당자로 등록하려고 하면 오류
        if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
            throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다!");
        }
        //새로운 담당자 등록하기
        Manager newManagerUser = new Manager(managerUser, todo);
        Manager savedManagerUser = managerRepository.save(newManagerUser);

        return new ManagerSaveResponse(
                savedManagerUser.getId(),
                new UserResponse(managerUser.getId(), managerUser.getEmail())
        );
    }
    //담당자 리스트 보여주는 함수: getManagers
    //이 함수는 어떤 일정에 누가 담당자로 등록되어 있는지 모두 보여주는 함수예요.
    //일정 찾기
    //먼저 일정 번호(todoId)로 그 일정을 찾고, 없으면 오류를 던져요.
    //담당자 리스트 가져오기
    //그 일정에 등록된 모든 담당자를 리스트로 가져와요. (리스트는 여러 개의 데이터를 모아놓은 것)
    //담당자 정보 준비
    //각 담당자의 정보를 하나하나 꺼내서 이름과 이메일을 확인해요.
    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }
    //담당자 삭제하는 함수: deleteManager
    //이 함수는 일정에서 담당자를 삭제하는 역할을 해요.
    //작성자 확인
    //이 부분도 먼저, 일정을 작성한 사람인지 확인해요. 작성자가 아니면 오류를 던져요.
    //해당 담당자가 진짜 맞는지 확인
    //삭제하려는 담당자가 이 일정에 맞게 등록된 사람이 맞는지 확인해요. 아니면 오류를 던져요.
    //담당자 삭제
    //담당자가 맞다면 그 담당자를 일정에서 삭제해요.
    @Transactional
    public void deleteManager(long userId, long todoId, long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다!");
        }

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다!");
        }

        managerRepository.delete(manager);
    }
}
