package com.eiou.mall.user.dto;

public record UserProfileResponse(
        Long id,
        String username,
        String nickname,
        Integer status
) {
}
