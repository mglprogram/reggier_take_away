package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miao.common.BaseContext;
import com.miao.common.Result;
import com.miao.pojo.ShoppingCart;
import com.miao.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-16
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
//        设置当前用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
//        查询当前菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
//        添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
//            添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
//        查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
//          已经存在，就在原有数量的基础上加一
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
//          不存在，添加购物车并number设置为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return Result.success(cartServiceOne);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public Result delete() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("清空购物车成功");
    }

    /**
     * 设置套餐或菜品减少
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        代表减少的是菜品数量
        if (dishId != null) {
//            确保查询的是当前用户的购物车中的指定菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId).eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart dishCar = shoppingCartService.getOne(queryWrapper);
            if (dishCar == null) {
                Result.error("购物车没有该菜品");

            }
            dishCar.setNumber(dishCar.getNumber() - 1);
            Integer latestNumber = dishCar.getNumber();
            if (latestNumber > 0) {
//                  如果最新的数量大于0，更新购物车记录
                shoppingCartService.updateById(dishCar);
            } else if (latestNumber == 0) {
//                如果最新的数量等于0，删除购物车记录
                shoppingCartService.removeById(dishCar.getId());
            } else {
                return Result.error("操作异常");
            }
            return Result.success(dishCar);
        }
        //        代表减少的是套餐数量
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null) {
//            确保查询的是当前用户的购物车中的指定菜品
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId).eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart setmealCar = shoppingCartService.getOne(queryWrapper);
            if (setmealCar == null) {
                Result.error("购物车没有该菜品");

            }
            setmealCar.setNumber(setmealCar.getNumber() - 1);
            Integer latestNumber = setmealCar.getNumber();
            if (latestNumber > 0) {
//                  如果最新的数量大于0，更新购物车记录
                shoppingCartService.updateById(setmealCar);
            } else if (latestNumber == 0) {
//                如果最新的数量等于0，删除购物车记录
                shoppingCartService.removeById(setmealCar.getId());
            } else {
                return Result.error("操作异常");
            }
            return Result.success(setmealCar);
        }
        return Result.error("操作失败");

    }
}
