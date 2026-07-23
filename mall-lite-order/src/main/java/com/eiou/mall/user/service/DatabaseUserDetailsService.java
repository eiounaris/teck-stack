package com.eiou.mall.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.eiou.mall.common.security.AuthenticatedUser;
import com.eiou.mall.user.entity.MallUser;
import com.eiou.mall.user.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final int STATUS_ENABLED = 1;

    private final UserMapper userMapper;

    public DatabaseUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MallUser user = userMapper.selectOne(Wrappers.<MallUser>lambdaQuery()
                .eq(MallUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                STATUS_ENABLED == user.getStatus()
        );
    }
}
