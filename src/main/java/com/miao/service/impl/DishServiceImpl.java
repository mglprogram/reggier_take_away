package com.miao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miao.dto.DishDto;
import com.miao.mapper.DishFlavorMapper;
import com.miao.pojo.Dish;
import com.miao.mapper.DishMapper;
import com.miao.pojo.DishFlavor;
import com.miao.service.DishFlavorService;
import com.miao.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional //@Transactional 注解可以确保这两个数据库操作在同一个事务中执行，要么都成功，要么都失败，从而保证数据的一致性和完整性。
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品的基本信息到dish表
        this.save(dishDto);
        Long dishId = dishDto.getId();
//        菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setDishId(dishId);
        });
//        保存菜品口味数据到菜品口味表dish——flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */

    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
//        查询菜品基本的信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);
//        查询当前菜品对应的口味信息，从dish_Flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);


        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
//        更新dish表的基本信息
        this.updateById(dishDto);
//        清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据--dish_flavor表进行insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setDishId(dishDto.getId());
            //在插入新的口味数据之前，清除原有的主键值，要不然造成
//            Duplicate entry '1858115886322892801' for key 'dish_flavor.PRIMARY'
            item.setId(null);
        });
        dishFlavorService.saveBatch(flavors);
    }
}
