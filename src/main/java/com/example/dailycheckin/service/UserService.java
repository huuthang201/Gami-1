package com.example.dailycheckin.service;

import com.example.dailycheckin.dto.CreateUserRequest;
import com.example.dailycheckin.dto.UserProfileResponse;
import com.example.dailycheckin.entity.User;

public interface UserService {
    User createUser(CreateUserRequest req);
    UserProfileResponse getProfile(Long userId);
    User getEntity(Long id);
    void adjustAndSave(User user);
}