package com.miao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miao.common.CustomException;
import com.miao.dto.SetmealDto;
import com.miao.pojo.Setmeal;
import com.miao.mapper.SetmealMapper;
import com.miao.pojo.SetmealDish;
import com.miao.service.SetmealDishService;
import com.miao.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });


//        保存套餐和菜品的关联信息，操作setmeal_dish,执行insert
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐，同时需要删除套餐和菜品关联的数据
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(queryWrapper);
        if (count > 0) {
//            如果存在状态为1的，抛出异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
//        如果可以删除，先删除套餐表中的套餐 setmeal
        this.removeByIds(ids);
//     删除关系表中的数据--setmeal_dish
//     delete from setmeal_dish where setmeal.id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, ids);
        //删除关系表中的数据--setmeal_dish
        setmealDishService.remove(setmealDishLambdaQueryWrapper);


    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     *
     * @return
     */

    @Override
    public SetmealDto getData(Long id) {
//        回显setmeal的基本表的信息
        Setmeal setmeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        if (setmeal != null) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }

        return null;
    }
}
