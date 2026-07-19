package com.eiou.mybatis.practice;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    void createTable();

    int insertUser(DemoUser user);

    int insertUsers(@Param("users") List<DemoUser> users);

    DemoUser findById(@Param("id") long id);

    List<DemoUser> findPageByStatus(
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    int updateStatus(@Param("id") long id, @Param("status") String status);
}
