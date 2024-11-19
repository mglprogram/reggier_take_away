package com.miao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miao.common.CustomException;
import com.miao.pojo.Category;
import com.miao.mapper.CategoryMapper;
import com.miao.pojo.Dish;
import com.miao.pojo.Setmeal;
import com.miao.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miao.service.DishService;
import com.miao.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id) {
//        构造条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        查询条件，根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishLambdaQueryWrapper);
//        查询当前分类是否关联了菜品，如果已经关联，抛出一个异常
        if (dishCount > 0){
//            已经关联菜品，抛出业务异常
                throw new CustomException("当前分类下关联了菜品，无法删除");
        }
        //        构造条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        查询条件，根据分类id查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setmealCount = dishService.count(dishLambdaQueryWrapper);
//        查询当前分类是否关联了套餐，如果已经关联，抛出一个异常
        if (setmealCount > 0){
//            已经关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，无法删除");
        }
//        正常删除分类
        super.removeById(id);

    }
}
