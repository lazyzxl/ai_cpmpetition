package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.UserPageQueryDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User getByUsername(String username);

    void update(User user);
    @Select("select * from user where id=#{id}")
    User getById(Long id);

    Page<User> page(UserPageQueryDTO employeePageQueryDTO);
    @Insert("insert into user (username,password,phone,status,role,profession,create_time,update_time)" +
            "values " +
            "(#{username},#{password},#{phone},#{status},#{role},#{profession},#{createTime},#{updateTime})")
    void insert(User user);
    @Select("select * from user where username=#{username} and phone=#{phone}")
    User getByUsernameAndPhone(String username, String phone);

    @Select("select count(*) from user")
    Integer count();
}
