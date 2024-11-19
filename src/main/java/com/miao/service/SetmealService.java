package com.miao.service;

import com.miao.dto.SetmealDto;
import com.miao.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品关联的数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);
    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    SetmealDto getData(Long id);
}
