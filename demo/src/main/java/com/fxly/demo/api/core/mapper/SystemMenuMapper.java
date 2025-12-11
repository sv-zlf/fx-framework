package com.fxly.demo.api.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fxly.demo.api.core.dto.MenuQueryDTO;
import com.fxly.demo.api.core.entity.SystemMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface SystemMenuMapper extends BaseMapper<SystemMenu> {

    IPage<SystemMenu> getPageList(IPage<SystemMenu> page, @Param("menuQuery") MenuQueryDTO menuQuery);

    List<SystemMenu> getMenuList(@Param("menuQuery") MenuQueryDTO menuQuery);

}
