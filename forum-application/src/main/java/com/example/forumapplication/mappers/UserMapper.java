package com.example.forumapplication.mappers;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.forumapplication.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private UserRepository repository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public User fromDto(UserDto dto, int id) {
        User repositoryUser = repository.findById(id);
        if (repositoryUser == null) {
            throw new EntityNotFoundException("User", id);
        }
        User user = fromDto(dto);
        user.setUsername(repositoryUser.getUsername());
        user.setRole_id(repositoryUser.getRole_id());
        user.setId(id);
        return user;
    }

    public User fromDto(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            user.setPhone(dto.getPhoneNumber());
        }
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        return userDto;
    }
}
