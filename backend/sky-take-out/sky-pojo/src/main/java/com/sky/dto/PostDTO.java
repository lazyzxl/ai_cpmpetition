package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String content;
    private String tag;
}