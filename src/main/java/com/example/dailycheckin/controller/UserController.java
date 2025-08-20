package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.CreateUserRequest;
import com.example.dailycheckin.dto.UserProfileResponse;
import com.example.dailycheckin.entity.User;
import com.example.dailycheckin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody CreateUserRequest req) {
        return userService.createUser(req);
    }

    @GetMapping("/{id}")
    public UserProfileResponse profile(@PathVariable Long id) {
        return userService.getProfile(id);
    }
}