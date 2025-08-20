package com.example.dailycheckin.repository;

import com.example.dailycheckin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}