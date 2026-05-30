package com.sky.vo;

import com.sky.entity.Information;
import com.sky.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompetitionVO {
    private Long id;//主键
    private String name;//比赛名称
    private String title;//比赛分类
    private String description;//描述
    private String url;//官网地址
    private String date;//举办时间
    private LocalDateTime updateTime;
    private String photo;
    private String photourl;
    private LocalDateTime createTime;
    private String organizer;//主办方
    private String location;//举办地点
    private Integer participantLimit;//参赛人数限制
    private String prize;//奖项设置
    private String contact;//联系方式
    private List<Post> postList;//相关帖子列表
    private List<Information> informationList;//消息列表
}
