package com.sky.vo;

import com.sky.entity.care;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<care> careList;//用户关注列表
}
