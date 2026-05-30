package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.UserPageQueryDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface UserMangerMapper {
    @Update("update user set status=#{status},update_time=#{updateTime} where id=#{id}")
    void update(Long id, LocalDateTime updateTime, Integer status);

    Page<User> page(UserPageQueryDTO userPageQueryDTO);

    void delete(Long id);
}
