package oleksii.stepaniak.noteswebapplication.service.mapper.impl;

import oleksii.stepaniak.noteswebapplication.dto.UserRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.UserResponseDto;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.service.mapper.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements DtoMapper<UserResponseDto, UserRequestDto, User> {
    @Override
    public UserResponseDto mapToDto(User model) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(model.getId());
        responseDto.setName(model.getName());
        responseDto.setEmail(model.getEmail());
        responseDto.setPassword(model.getPassword());
        return responseDto;
    }

    @Override
    public User mapToModel(UserRequestDto requestDto) {
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        return user;
    }
}
