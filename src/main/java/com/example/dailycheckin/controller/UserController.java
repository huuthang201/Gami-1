package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.ApiResponse;
import com.example.dailycheckin.dto.CreateUserRequest;
import com.example.dailycheckin.dto.UserProfileResponse;
import com.example.dailycheckin.entity.User;
import com.example.dailycheckin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponse<User> create(@Valid @RequestBody CreateUserRequest req,
                                    HttpServletRequest request) {
        User u = userService.createUser(req);
        return ApiResponse.created(u, request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfileResponse> profile(@PathVariable Long id,
                                                    HttpServletRequest request) {
        UserProfileResponse profile = userService.getProfile(id);
        return ApiResponse.ok(profile, request.getRequestURI());
    }
}