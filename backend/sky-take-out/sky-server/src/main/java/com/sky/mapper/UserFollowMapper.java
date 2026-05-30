package com.sky.mapper;

import com.sky.entity.UserFollow;
import com.sky.vo.UserFollowVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFollowMapper {

    /**
     * 关注用户
     */
    @Insert("insert into user_follow (user_id, follow_user_id, create_time) " +
            "values (#{userId}, #{followUserId}, #{createTime})")
    void insert(UserFollow userFollow);

    /**
     * 取消关注
     */
    @Delete("delete from user_follow where user_id = #{userId} and follow_user_id = #{followUserId}")
    void delete(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 检查是否已关注
     */
    @Select("select count(*) from user_follow where user_id = #{userId} and follow_user_id = #{followUserId}")
    int countByUserIdAndFollowUserId(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 获取用户的关注列表
     */
    @Select("select uf.id, uf.follow_user_id, u.username as follow_username, u.profession as follow_user_profession, uf.create_time " +
            "from user_follow uf " +
            "left join user u on uf.follow_user_id = u.id " +
            "where uf.user_id = #{userId} " +
            "order by uf.create_time desc")
    List<UserFollowVO> getFollowList(Long userId);

    /**
     * 获取用户的粉丝列表
     */
    @Select("select uf.id, uf.user_id as follow_user_id, u.username as follow_username, u.profession as follow_user_profession, uf.create_time " +
            "from user_follow uf " +
            "left join user u on uf.user_id = u.id " +
            "where uf.follow_user_id = #{userId} " +
            "order by uf.create_time desc")
    List<UserFollowVO> getFansList(Long userId);

    /**
     * 获取粉丝数量
     */
    @Select("select count(*) from user_follow where follow_user_id = #{userId}")
    int countFans(Long userId);

    /**
     * 获取关注数量
     */
    @Select("select count(*) from user_follow where user_id = #{userId}")
    int countFollow(Long userId);

    /**
     * 获取所有用户（用于关注搜索）
     */
    @Select("select id, id as follow_user_id, username as follow_username, profession as follow_user_profession from user where id != #{userId} and status = 1")
    List<UserFollowVO> getAllUsers(Long userId);
}
