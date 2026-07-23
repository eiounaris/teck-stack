package com.eiou.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eiou.mall.user.entity.MallUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<MallUser> {
}
