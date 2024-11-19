package com.miao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-17
 */
@Getter
@Setter
@TableName("order_detail")
@ApiModel(value = "OrderDetail对象", description = "订单明细表")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("名字")
    @TableField("name")
    private String name;

    @ApiModelProperty("图片")
    @TableField("image")
    private String image;

    @ApiModelProperty("订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty("菜品id")
    @TableField("dish_id")
    private Long dishId;

    @ApiModelProperty("套餐id")
    @TableField("setmeal_id")
    private Long setmealId;

    @ApiModelProperty("口味")
    @TableField("dish_flavor")
    private String dishFlavor;

    @ApiModelProperty("数量")
    @TableField("number")
    private Integer number;

    @ApiModelProperty("金额")
    @TableField("amount")
    private BigDecimal amount;
}
