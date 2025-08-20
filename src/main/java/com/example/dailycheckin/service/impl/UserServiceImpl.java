package com.example.dailycheckin.service.impl;

import com.example.dailycheckin.dto.CreateUserRequest;
import com.example.dailycheckin.dto.UserProfileResponse;
import com.example.dailycheckin.entity.MonthlyCheckinSummary;
import com.example.dailycheckin.entity.User;
import com.example.dailycheckin.exception.ApiException;
import com.example.dailycheckin.repository.MonthlyCheckinSummaryRepository;
import com.example.dailycheckin.repository.UserRepository;
import com.example.dailycheckin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final MonthlyCheckinSummaryRepository summaryRepo;

    @Override
    @Transactional
    public User createUser(CreateUserRequest req) {
        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .totalPoints(0)
                .build();
        return userRepo.save(u);
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = getEntity(userId);
        String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        var summary = summaryRepo.findByUserIdAndYearMonth(userId, ym).orElse(null);
        int count = summary == null ? 0 : summary.getCheckinCount();
        int max = 7;
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .totalPoints(user.getTotalPoints())
                .currentMonthCheckinCount(count)
                .currentMonthMax(max)
                .remaining(Math.max(0, max - count))
                .build();
    }

    @Override
    public User getEntity(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User not found"));
    }

    @Override
    @Transactional
    public void adjustAndSave(User user) {
        userRepo.save(user);
    }
}