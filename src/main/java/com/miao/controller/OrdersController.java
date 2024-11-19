package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miao.common.BaseContext;
import com.miao.common.Result;
import com.miao.dto.OrderDto;
import com.miao.pojo.OrderDetail;
import com.miao.pojo.Orders;
import com.miao.service.OrderDetailService;
import com.miao.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-17
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Orders orders) {
        log.info("orders: {}", orders);
        ordersService.submit(orders);
        return Result.success("下单成功");
    }
    //抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    /**
     * 用户端展示自己的订单分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result userPage(@RequestParam int page, @RequestParam int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        添加用户id作为查询条件
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
//        根据订单时间降序排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage, queryWrapper);
        BeanUtils.copyProperties(ordersPage, orderDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrderDto> orderDtos = records.stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item,orderDto);
            //通过OrderId查询对应的OrderDetail
            Long orderId = item.getId();
            List<OrderDetail> orderDetails = this.getOrderDetailListByOrderId(orderId);
            //对orderDto进行orderDetails属性赋值
            orderDto.setOrderDetails(orderDetails);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(orderDtos);
        return Result.success(orderDtoPage);
    }

    /**
     * 后端查询订单明细
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(@RequestParam int page, @RequestParam int pageSize,String number,String beginTime,String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件 动态sql 字符串使用StringUtils.isNotEmpty这个方法来判断
        queryWrapper.like(number != null, Orders::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        ordersService.page(ordersPage, queryWrapper);
        return Result.success(ordersPage);
    }
    @PutMapping
    public Result updateStatus(@RequestBody Orders orders) {
        if (orders.getId() == null || orders.getStatus() == null)
            return Result.error("请求异常");
        ordersService.updateById(orders);
        return Result.success("订单状态修改成功");

    }

}
