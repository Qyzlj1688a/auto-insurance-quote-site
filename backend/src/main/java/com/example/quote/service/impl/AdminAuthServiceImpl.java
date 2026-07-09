package com.example.quote.service.impl;

import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;
import com.example.quote.entity.AdminUser;
import com.example.quote.exception.BusinessException;
import com.example.quote.repository.AdminUserRepository;
import com.example.quote.security.JwtTokenProvider;
import com.example.quote.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理者認証サービス実装クラス。
 *
 * <p>ブルートフォース攻撃対策として、同一loginIdでの連続ログイン失敗回数を
 * インメモリで追跡し、上限を超えた場合は一定時間ログインを拒否するロックアウト機構を備える。
 * 複数インスタンス構成（水平スケール）を行う場合は、この状態はインスタンスごとに独立するため、
 * より厳密な対策が必要になれば外部ストア（Redis等）への置き換えを検討すること（既知の制限）。
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private static final String UNAUTHORIZED_MESSAGE = "ログインIDまたはパスワードが正しくありません。";

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final int maxFailedAttempts;
    private final Duration lockoutDuration;
    private final Clock clock;

    private final ConcurrentHashMap<String, LoginAttemptState> loginAttempts = new ConcurrentHashMap<>();

    @Autowired
    public AdminAuthServiceImpl(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            @Value("${app.login-lockout.max-failed-attempts:5}") int maxFailedAttempts,
            @Value("${app.login-lockout.lockout-minutes:15}") long lockoutMinutes
    ) {
        this(adminUserRepository, passwordEncoder, tokenProvider, maxFailedAttempts, lockoutMinutes, Clock.systemUTC());
    }

    /**
     * テストなど、時刻や上限値を明示的に制御したい場合に使用するコンストラクタ。
     */
    public AdminAuthServiceImpl(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            int maxFailedAttempts,
            long lockoutMinutes,
            Clock clock
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockoutDuration = Duration.ofMinutes(lockoutMinutes);
        this.clock = clock;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        String loginKey = normalizeLoginId(request.getLoginId());
        rejectIfLocked(loginKey);

        AdminUser adminUser = adminUserRepository.findByLoginIdAndActiveTrue(request.getLoginId())
                .orElse(null);

        if (adminUser == null || !passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            recordFailure(loginKey);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", UNAUTHORIZED_MESSAGE);
        }

        loginAttempts.remove(loginKey);

        String token = tokenProvider.generateToken(adminUser.getLoginId());

        AdminLoginResponse response = new AdminLoginResponse();
        response.setLoginId(adminUser.getLoginId());
        response.setDisplayName(adminUser.getDisplayName());
        response.setMessage("ログインに成功しました。");
        response.setToken(token);
        return response;
    }

    private void rejectIfLocked(String loginKey) {
        LoginAttemptState state = loginAttempts.get(loginKey);
        if (state == null || state.lockedUntil == null) {
            return;
        }
        if (clock.instant().isBefore(state.lockedUntil)) {
            throw new BusinessException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "LOGIN_LOCKED",
                    "ログイン試行回数が上限を超えたため、一時的にロックされています。しばらく時間をおいて再度お試しください。"
            );
        }
        // ロック期間を過ぎている場合は状態をリセットして通常のログイン処理を継続する
        loginAttempts.remove(loginKey);
    }

    private void recordFailure(String loginKey) {
        LoginAttemptState state = loginAttempts.computeIfAbsent(loginKey, key -> new LoginAttemptState());
        synchronized (state) {
            state.failedCount++;
            if (state.failedCount >= maxFailedAttempts) {
                state.lockedUntil = clock.instant().plus(lockoutDuration);
                state.failedCount = 0;
            }
        }
    }

    private String normalizeLoginId(String loginId) {
        return loginId == null ? "" : loginId.trim().toLowerCase(Locale.ROOT);
    }

    private static final class LoginAttemptState {
        private int failedCount;
        private volatile Instant lockedUntil;
    }
}
