package com.example.dailycheckin.service.impl;

import com.example.dailycheckin.dto.DeductPointRequest;
import com.example.dailycheckin.dto.TransactionItem;
import com.example.dailycheckin.dto.TransactionPageResponse;
import com.example.dailycheckin.entity.PointTransaction;
import com.example.dailycheckin.entity.TransactionType;
import com.example.dailycheckin.entity.User;
import com.example.dailycheckin.exception.ApiException;
import com.example.dailycheckin.repository.PointTransactionRepository;
import com.example.dailycheckin.service.TransactionService;
import com.example.dailycheckin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final PointTransactionRepository txRepo;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public TransactionPageResponse list(Long userId, int page, int size) {
        var p = txRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return TransactionPageResponse.builder()
                .page(page)
                .size(size)
                .total(p.getTotalElements())
                .items(p.map(this::toItem).getContent())
                .build();
    }

    private TransactionItem toItem(PointTransaction tx) {
        return TransactionItem.builder()
                .id(tx.getId())
                .type(tx.getType().name())
                .delta(tx.getDelta())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .refId(tx.getRefId())
                .createdAt(tx.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public long deduct(Long userId, DeductPointRequest req) {
        User user = userService.getEntity(userId);
        if (req.getPoints() <= 0) throw ApiException.bad("INVALID_POINTS", "points must > 0");
        if (user.getTotalPoints() < req.getPoints()) {
            throw ApiException.conflict("INSUFFICIENT_BALANCE", "Not enough points");
        }
        user.setTotalPoints(user.getTotalPoints() - req.getPoints());
        userService.adjustAndSave(user);
        PointTransaction tx = PointTransaction.builder()
                .userId(userId)
                .type(TransactionType.DEDUCT)
                .delta(-req.getPoints())
                .balanceAfter(user.getTotalPoints())
                .description(req.getReason())
                .build();
        txRepo.save(tx);
        return user.getTotalPoints();
    }
}