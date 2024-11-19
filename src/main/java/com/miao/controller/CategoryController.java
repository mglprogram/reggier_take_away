package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miao.common.Result;
import com.miao.pojo.Category;
import com.miao.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-14
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品和套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(int page,int pageSize){
        log.info("page:{},pageSize:{}", page, pageSize);
//        分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加排序条件，根据sort排序
        queryWrapper.orderByAsc(Category::getSort);
//        进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);

    }

    /**
     * 根据id删除分类，该分类区分关联了菜品还是套餐
     * 自定义了一个remove的方法
     * @param id
     * @return
     */
    @DeleteMapping
    public Result delete(Long id) {
        log.info("删除分类，id:{}", id);
        categoryService.remove(id);
        return Result.success("分类信息删除成功");

    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Category category) {
        log.info("修改分类的信息:{}", category);
        categoryService.updateById(category);
        return Result.success("修改分类成功");
    }

    /**
     * 根据条件查询分类的数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }

}
