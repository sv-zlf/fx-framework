package com.fxly.demo.api.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxly.demo.api.core.entity.SystemCronTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务Mapper
 * @author admin
 */
@Mapper
public interface SystemCronTaskMapper extends BaseMapper<SystemCronTask> {

}