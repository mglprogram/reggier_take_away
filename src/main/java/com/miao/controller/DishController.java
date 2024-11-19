package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miao.common.Result;
import com.miao.dto.DishDto;
import com.miao.pojo.Category;
import com.miao.pojo.Dish;
import com.miao.pojo.DishFlavor;
import com.miao.service.CategoryService;
import com.miao.service.DishFlavorService;
import com.miao.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@RestController
@RequestMapping("/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    @ApiOperation(value = "新增菜品接口")
    public Result save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品缓存数据
        //Set keys = redisTemplate.keys("dish=*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
//        String key="dish="+dishDto.getCategoryId()+"_"+dishDto.getStatus();
//        redisTemplate.delete(key);
        return Result.success("新增菜品成功");

    }

    /**
     * 菜品的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品的分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "菜品名称",required = false)
    })
    public Result page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
//        对象拷贝并且不拷贝records
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();
        // 手动处理records
        records.forEach(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
//            根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            dishDtoList.add(dishDto);
        });

//        List<DishDto> dishDtoList = records.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(item, dishDto);
//            Long categoryId = item.getCategoryId();//分类id
////            根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//            if (category != null) {
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            return dishDto;
//        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return Result.success(dishDtoPage);


    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "id查询菜品及对应的口味信息接口")
        public Result getById(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    @ApiOperation(value = "修改菜品接口")
    public Result update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
//      清理所有菜品缓存数据
//        Set keys = redisTemplate.keys("dish=*");
//        redisTemplate.delete(keys);
        //清理某个分类下面的菜品缓存数据
//        String key = "dish=" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
//        redisTemplate.delete(key);


        return Result.success("修改菜品成功");
    }

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品停售或起售接口")
    public Result updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
//        log.info("status:{}",status);
//        log.info("ids:{}",ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        select * from dish where id in (1,2,3)
        queryWrapper.in(ids != null, Dish::getId, ids);
//        根据数据进行批量查询
        List<Dish> list = dishService.list(queryWrapper);
        list.forEach(item -> {
            item.setStatus(status);
            dishService.updateById(item);
        });
        return Result.success("菜品售卖状态修改成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache", key = "#dish.categoryId + '_' + #dish.status")
    @ApiOperation(value = "菜品信息接口")
    public Result list(Dish dish) {
        List<DishDto> dishDtoList = null;
//        动态构造key
//        String key = "dish=" + dish.getCategoryId() + "_" + dish.getStatus();
////        先从redis获取缓存数据
//        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
//        if (dishDtoList != null) {
////            如果存在，直接返回，无需查询数据库
//            return Result.success(dishDtoList);
//        }

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        //添加条件，查询条件为1(起售)
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类Id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品Id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
//        如果不存在，查询数据库，将查询的菜品缓存到redis
//        redisTemplate.opsForValue().set(key, dishDtoList,60, TimeUnit.MINUTES);

        return Result.success(dishDtoList);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    @ApiOperation(value = "删除菜品接口")
    public Result delete(@RequestParam("ids") List<Long> ids) {
//        log.info("ids:{}",ids);
//        删除菜品，逻辑删除
        dishService.removeByIds(ids);
//        也要删除菜品所对应的口味 也是逻辑删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);
        return Result.success("菜品及对应的口味删除成功");
    }

}
