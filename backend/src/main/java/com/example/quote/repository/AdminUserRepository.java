package com.example.quote.repository;

import com.example.quote.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Administrator user repository.
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByLoginIdAndActiveTrue(String loginId);
}
