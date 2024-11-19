package com.miao.dto;

import com.miao.pojo.OrderDetail;
import com.miao.pojo.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author 缪广亮
 * @version 1.0
 */
@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}

