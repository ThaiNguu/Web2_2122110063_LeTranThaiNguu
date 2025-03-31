package com.example.letranthainguu.backend.service;

import com.example.letranthainguu.backend.dto.UserDTO;
import com.example.letranthainguu.backend.dto.UserReponse;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);

    UserReponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    UserDTO getUserById(Long userId);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    String deleteUser(Long userId);

    UserDTO getUserByEmail(String email);

}
