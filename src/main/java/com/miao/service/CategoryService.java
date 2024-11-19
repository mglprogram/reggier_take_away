package com.miao.service;

import com.miao.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}
