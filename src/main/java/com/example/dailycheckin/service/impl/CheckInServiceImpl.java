package com.example.dailycheckin.service.impl;

import com.example.dailycheckin.dto.CheckInResponse;
import com.example.dailycheckin.dto.DayStatus;
import com.example.dailycheckin.dto.MonthCheckInStatusResponse;
import com.example.dailycheckin.dto.MonthlyDailyStatusResponse;
import com.example.dailycheckin.dto.DailyStatus;
import com.example.dailycheckin.entity.CheckIn;
import com.example.dailycheckin.entity.MonthlyCheckinSummary;
import com.example.dailycheckin.entity.PointConfig;
import com.example.dailycheckin.entity.PointTransaction;
import com.example.dailycheckin.entity.TimeWindow;
import com.example.dailycheckin.entity.TransactionType;
import com.example.dailycheckin.entity.User;
import com.example.dailycheckin.exception.ApiException;
import com.example.dailycheckin.repository.CheckInRepository;
import com.example.dailycheckin.repository.MonthlyCheckinSummaryRepository;
import com.example.dailycheckin.repository.PointConfigRepository;
import com.example.dailycheckin.repository.PointTransactionRepository;
import com.example.dailycheckin.repository.TimeWindowRepository;
import com.example.dailycheckin.service.CheckInService;
import com.example.dailycheckin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final UserService userService;
    private final CheckInRepository checkInRepo;
    private final PointConfigRepository pointConfigRepo;
    private final MonthlyCheckinSummaryRepository summaryRepo;
    private final PointTransactionRepository transactionRepo;
    private final TimeWindowRepository timeWindowRepo;
    private final RedissonClient redisson;

    @Value("${app.checkin.max-per-month:7}")
    private int maxPerMonth;

    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter MONTH_PARAM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional
    public CheckInResponse checkIn(Long userId) {
        User user = userService.getEntity(userId);
        LocalDate today = LocalDate.now();
        String lockKey = "lock:checkin:" + userId + ":" + today;
        RLock lock = redisson.getLock(lockKey);
        boolean locked = false;
        try {
            try {
                locked = lock.tryLock(3, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw ApiException.conflict("LOCK_INTERRUPTED", "Interrupted while acquiring lock");
            }
            if (!locked) {
                throw ApiException.conflict("LOCK_FAILED", "Unable to acquire check-in lock");
            }

            if (checkInRepo.findByUserIdAndCheckInDate(userId, today).isPresent()) {
                throw ApiException.conflict("ALREADY_CHECKED_IN", "User already checked in today");
            }

            validateTimeWindow();

            String ym = today.format(YM_FMT);
            MonthlyCheckinSummary summary = summaryRepo.findByUserIdAndYearMonth(userId, ym)
                    .orElseGet(() -> summaryRepo.save(MonthlyCheckinSummary.builder()
                            .userId(userId)
                            .yearMonth(ym)
                            .checkinCount(0)
                            .totalAwardedPoints(0)
                            .build()));

            if (summary.getCheckinCount() >= maxPerMonth) {
                throw ApiException.conflict("MONTHLY_LIMIT_REACHED", "Monthly check-in quota reached");
            }

            int orderIndex = summary.getCheckinCount() + 1;
            int points = pointConfigRepo.findById(orderIndex)
                    .filter(pc -> Boolean.TRUE.equals(pc.getEnabled()))
                    .map(PointConfig::getPoints)
                    .orElseThrow(() -> ApiException.bad("POINT_CONFIG_NOT_FOUND",
                            "Point config missing for order " + orderIndex));

            CheckIn ci = checkInRepo.save(CheckIn.builder()
                    .userId(userId)
                    .checkInDate(today)
                    .awardedPoints(points)
                    .build());

            user.setTotalPoints(user.getTotalPoints() + points);
            userService.adjustAndSave(user);

            summary.setCheckinCount(orderIndex);
            summary.setLastCheckinDate(today);
            summary.setTotalAwardedPoints(summary.getTotalAwardedPoints() + points);
            summaryRepo.save(summary);

            PointTransaction tx = PointTransaction.builder()
                    .userId(userId)
                    .type(TransactionType.CHECKIN)
                    .delta(points)
                    .balanceAfter(user.getTotalPoints())
                    .description("Check-in day order " + orderIndex)
                    .refId(ci.getId())
                    .build();
            transactionRepo.save(tx);

            boolean nextEligible = orderIndex < maxPerMonth;
            return CheckInResponse.builder()
                    .date(today)
                    .awardedPoints(points)
                    .monthlyCount(orderIndex)
                    .monthlyMax(maxPerMonth)
                    .totalPoints(user.getTotalPoints())
                    .nextEligible(nextEligible)
                    .build();

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("uk_ci_user_date")) {
                throw ApiException.conflict("ALREADY_CHECKED_IN", "User already checked in today");
            }
            throw e;
        } finally {
            if (locked) {
                try { lock.unlock(); } catch (Exception ignore) {}
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public MonthCheckInStatusResponse getMonthStatus(Long userId, String month) {
        LocalDate now = LocalDate.now();
        YearMonth target = (month == null || month.isBlank())
                ? YearMonth.of(now.getYear(), now.getMonth())
                : YearMonth.parse(month, MONTH_PARAM_FMT);

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<CheckIn> list = checkInRepo.findByUserIdAndCheckInDateBetween(userId, start, end);
        Map<Integer, CheckIn> dayMap = list.stream()
                .collect(Collectors.toMap(ci -> ci.getCheckInDate().getDayOfMonth(), ci -> ci));

        List<DayStatus> days = IntStream.rangeClosed(1, end.getDayOfMonth())
                .mapToObj(d -> {
                    CheckIn ci = dayMap.get(d);
                    return DayStatus.builder()
                            .day(d)
                            .checkedIn(ci != null)
                            .awardedPoints(ci != null ? ci.getAwardedPoints() : null)
                            .time(null)
                            .build();
                })
                .collect(Collectors.toList());

        int count = list.size();
        return MonthCheckInStatusResponse.builder()
                .month(target.toString())
                .days(days)
                .count(count)
                .max(maxPerMonth)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyDailyStatusResponse getMonthlyDailyStatuses(Long userId, String month) {
        LocalDate now = LocalDate.now();
        YearMonth target = (month == null || month.isBlank())
                ? YearMonth.of(now.getYear(), now.getMonth())
                : YearMonth.parse(month, MONTH_PARAM_FMT);

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<CheckIn> list = checkInRepo.findByUserIdAndCheckInDateBetween(userId, start, end);
        Map<LocalDate, CheckIn> map = list.stream()
                .collect(Collectors.toMap(CheckIn::getCheckInDate, ci -> ci));

        ZoneId zone = ZoneId.systemDefault();

        List<DailyStatus> days = IntStream.rangeClosed(1, end.getDayOfMonth())
                .mapToObj(d -> {
                    LocalDate date = start.withDayOfMonth(d);
                    CheckIn ci = map.get(date);
                    return DailyStatus.builder()
                            .date(date)
                            .checkedIn(ci != null)
                            .awardedPoints(ci != null ? ci.getAwardedPoints() : null)
                            .checkInId(ci != null ? ci.getId() : null)
                            .checkInTime(ci != null && ci.getCreatedAt() != null
                                    ? LocalDateTime.ofInstant(ci.getCreatedAt(), zone)
                                    : null)
                            .build();
                })
                .collect(Collectors.toList());

        int checkedCount = list.size();

        return MonthlyDailyStatusResponse.builder()
                .month(target.toString())
                .totalDays(end.getDayOfMonth())
                .checkedInCount(checkedCount)
                .maxPerMonth(maxPerMonth)
                .remaining(Math.max(0, maxPerMonth - checkedCount))
                .days(days)
                .build();
    }

    private void validateTimeWindow() {
        LocalTime now = LocalTime.now();
        List<TimeWindow> windows = timeWindowRepo.findAll(Sort.unsorted());
        boolean ok = windows.stream()
                .filter(w -> Boolean.TRUE.equals(w.getEnabled()))
                .anyMatch(w -> !now.isBefore(w.getStartTime()) && now.isBefore(w.getEndTime()));
        if (!ok) {
            throw ApiException.bad("OUT_OF_TIME_WINDOW", "Not in allowed check-in time window");
        }
    }
}