package com.example.Library_Management_System.mapper;

import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.modal.User;

import com.example.Library_Management_System.payload.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.Library_Management_System.payload.dto.UserDTO;

@Component
public class UserMapper {
    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setRole(user.getRole());


        return userDTO;
    }

    public static List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static Set<UserDTO> toDTOSet(Set<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public static User toEntity(UserDTO userDTO) {
        User createdUser = new User();
        createdUser.setEmail(userDTO.getEmail());
        createdUser.setPassword(userDTO.getPassword());
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setPhone(userDTO.getPhone());
        createdUser.setFullName(userDTO.getFullName());
        createdUser.setRole(userDTO.getRole());

        return createdUser;
    }

    public void updateEntityFromDTO(User user, UserDTO userDTO) throws UserException {

        if (userDTO.getId() == null) {
            return;
        }

        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
    }
}
