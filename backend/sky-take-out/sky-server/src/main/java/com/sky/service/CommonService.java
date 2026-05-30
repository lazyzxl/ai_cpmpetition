package com.sky.service;

import com.sky.dto.SearchDTO;
import com.sky.result.PageResult;

public interface CommonService {
    PageResult search(SearchDTO competitionSearchDTO);
}
