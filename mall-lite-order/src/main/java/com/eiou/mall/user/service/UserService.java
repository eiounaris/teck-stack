package com.eiou.mall.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.eiou.mall.common.exception.BusinessException;
import com.eiou.mall.common.exception.ErrorCode;
import com.eiou.mall.common.security.AuthenticatedUser;
import com.eiou.mall.common.security.JwtTokenService;
import com.eiou.mall.common.security.SecurityUtils;
import com.eiou.mall.user.dto.LoginRequest;
import com.eiou.mall.user.dto.LoginResponse;
import com.eiou.mall.user.dto.RegisterRequest;
import com.eiou.mall.user.dto.UserProfileResponse;
import com.eiou.mall.user.entity.MallUser;
import com.eiou.mall.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final int STATUS_ENABLED = 1;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        Long existing = userMapper.selectCount(Wrappers.<MallUser>lambdaQuery()
                .eq(MallUser::getUsername, request.username()));
        if (existing > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Username already exists");
        }

        MallUser user = new MallUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setStatus(STATUS_ENABLED);
        userMapper.insert(user);
        return toProfile(user);
    }

    public LoginResponse login(LoginRequest request) {
        MallUser user = userMapper.selectOne(Wrappers.<MallUser>lambdaQuery()
                .eq(MallUser::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid username or password");
        }
        if (STATUS_ENABLED != user.getStatus()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "User is disabled");
        }

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                true
        );
        String token = jwtTokenService.generateToken(authenticatedUser);
        return new LoginResponse(token, "Bearer", jwtTokenService.expiresInSeconds(), toProfile(user));
    }

    public UserProfileResponse currentUser() {
        AuthenticatedUser user = SecurityUtils.currentUser();
        return new UserProfileResponse(user.id(), user.getUsername(), user.nickname(), STATUS_ENABLED);
    }

    private UserProfileResponse toProfile(MallUser user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getNickname(), user.getStatus());
    }
}
