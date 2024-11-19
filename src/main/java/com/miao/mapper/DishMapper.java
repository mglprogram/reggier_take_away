package com.miao.mapper;

import com.miao.pojo.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
