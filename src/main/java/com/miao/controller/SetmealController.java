package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miao.common.Result;
import com.miao.dto.DishDto;
import com.miao.dto.SetmealDto;
import com.miao.pojo.Category;
import com.miao.pojo.Dish;
import com.miao.pojo.Setmeal;
import com.miao.pojo.SetmealDish;
import com.miao.service.CategoryService;
import com.miao.service.DishService;
import com.miao.service.SetmealDishService;
import com.miao.service.SetmealService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-15
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息:{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return Result.success("新增套餐成功");
    }

    /**
     * 套餐的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result page(int page, int pageSize,String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDishPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDishPage,"records");
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        setmealRecords.forEach(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
//            根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            setmealDtoList.add(setmealDto);
        });
        setmealDishPage.setRecords(setmealDtoList);
        return Result.success(setmealDishPage);
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids) {
        Setmeal setmeal = new Setmeal();
        ids.forEach(id -> {
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        });
        return Result.success("套餐状态修改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     * @CacheEvict(value = "setmealCache", allEntries = true)
     * CacheEvict:删除redis的所有套餐缓存
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.removeWithDish(ids);
        return Result.success("套餐删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     * @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
     * @Cacheable：将第一次从数据库得到的数据缓存到redis
     * value:redis的缓存名称；key：这类缓存里面某个缓存的key
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public Result list( Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);


    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result getById(@PathVariable("id") Long id) {
//        log.info("id:{}", id);
        SetmealDto setmealDto = setmealService.getData(id);
        return Result.success(setmealDto);
    }

    /**
     * 套餐修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result update(@RequestBody SetmealDto setmealDto){

        if (setmealDto==null){
            return Result.error("请求异常");
        }

        if (setmealDto.getSetmealDishes()==null){
            return Result.error("套餐没有菜品,请添加套餐");
        }

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);

        //为setmeal_dish表填充相关的属性
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);

        return Result.success("套餐修改成功");
    }

    @GetMapping("/dish/{id}")
    public Result dish(@PathVariable("id") Long SetmealId){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,SetmealId);
        //获取套餐里面的所有菜品，这个就是SetmealDish表里面的数据
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        List<DishDto> dishDtoList = setmealDishList.stream().map((setmealDish -> {
            DishDto dishDto = new DishDto();
            //其实这个BeanUtils的拷贝是浅拷贝
            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等菜品的基本信息
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            if (dish != null) {
                BeanUtils.copyProperties(dish, dishDto, "id"); // 避免覆盖原有的id字段
            }
            return dishDto;
        })).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }

}
